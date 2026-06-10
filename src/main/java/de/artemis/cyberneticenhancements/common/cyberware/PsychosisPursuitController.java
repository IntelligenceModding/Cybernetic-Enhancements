package de.artemis.cyberneticenhancements.common.cyberware;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class PsychosisPursuitController {
    private static final String WANDER_YAW_KEY = "cyberneticenhancements.psychosis_wander_yaw";
    private static final double IMMEDIATE_VISIBLE_TARGET_RANGE = 10.0D;
    private static final double MAX_HIDDEN_TARGET_DIRECT_DISTANCE = CyberwareBalance.doubleValue("cyberstrain.psychosis.hidden_target_max_direct_distance");
    private static final double MAX_HIDDEN_TARGET_PATH_DISTANCE = CyberwareBalance.doubleValue("cyberstrain.psychosis.hidden_target_max_path_distance");
    private static final long PATH_RECALC_INTERVAL_TICKS = 10L;
    private static final long PATH_COLLISION_REPATH_TICKS = 4L;
    private static final long PATH_STUCK_REPATH_TICKS = 12L;
    private static final long PATH_JUMP_HOLD_TICKS = 6L;
    private static final double PATH_NODE_DIRECTION_CHECK_DISTANCE = 2.0D;
    private static final double PATH_PROGRESS_DISTANCE_SQR = 0.18D * 0.18D;
    private static final Map<UUID, PathState> PATH_STATES = new HashMap<>();

    private PsychosisPursuitController() {
    }

    public static LivingEntity findTarget(Player player) {
        List<LivingEntity> targets = player.level().getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(24.0D),
                entity -> entity != player && entity.isAlive() && isValidTarget(entity));

        LivingEntity recentAggressor = player.getLastHurtByMob();
        List<TargetCandidate> candidates = targets.stream()
                .map(entity -> createTargetCandidate(player, entity, recentAggressor))
                .filter(candidate -> candidate != null)
                .toList();

        Integer bestPriority = candidates.stream()
                .map(TargetCandidate::priority)
                .min(Integer::compareTo)
                .orElse(null);
        if (bestPriority == null) {
            return null;
        }

        List<TargetCandidate> bestPriorityCandidates = candidates.stream()
                .filter(candidate -> candidate.priority() == bestPriority)
                .toList();

        LivingEntity immediateTarget = selectVisibleTarget(bestPriorityCandidates);
        if (immediateTarget != null) {
            return immediateTarget;
        }

        return bestPriorityCandidates.stream()
                .sorted(Comparator
                        .comparingDouble(TargetCandidate::pathDistance)
                        .thenComparingInt(candidate -> candidate.recentAggressor() ? 0 : 1)
                        .thenComparingDouble(TargetCandidate::directDistanceSqr))
                .map(TargetCandidate::entity)
                .findFirst()
                .orElse(null);
    }

    public static PursuitIntent buildHuntIntent(Player player, LivingEntity target, long gameTime) {
        boolean targetVisible = player.hasLineOfSight(target);
        if (targetVisible) {
            return buildDirectHuntIntent(player, target);
        }

        PathFollowTarget pathFollowTarget = resolvePathTargetPosition(player, target, gameTime);
        Vec3 pathTargetPosition = pathFollowTarget.position();
        Vec3 moveDelta = pathTargetPosition.subtract(player.position());
        Vec3 moveHorizontal = flatten(moveDelta);
        if (moveHorizontal.lengthSqr() < 0.0001D) {
            return new PursuitIntent(player.getYRot(), player.getXRot(), 0.0F, 0.0F, false, true);
        }

        Vec3 lookTargetPosition = pathTargetPosition.add(0.0D, Math.max(0.4D, target.getBbHeight() * 0.25D), 0.0D);
        Vec3 lookDelta = lookTargetPosition.subtract(player.getEyePosition());
        Vec3 lookHorizontal = flatten(lookDelta);
        if (lookHorizontal.lengthSqr() < 0.0001D) {
            lookHorizontal = moveHorizontal;
        }

        float lookYaw = (float) (Math.toDegrees(Math.atan2(lookHorizontal.z, lookHorizontal.x)) - 90.0D);
        float pitch = (float) Mth.clamp(-Math.toDegrees(Math.atan2(lookDelta.y, Math.max(0.001D, lookHorizontal.length()))), -35.0D, 35.0D);

        Vec3 moveDirection = moveHorizontal.normalize();
        float moveYaw = (float) (Math.toDegrees(Math.atan2(moveDirection.z, moveDirection.x)) - 90.0D);
        float yawDifference = Mth.wrapDegrees(moveYaw - lookYaw);
        float yawDifferenceRadians = yawDifference * Mth.DEG_TO_RAD;
        float forward = Mth.clamp((float) Math.cos(yawDifferenceRadians), 0.35F, 1.0F);
        float strafe = Mth.clamp((float) -Math.sin(yawDifferenceRadians), -1.0F, 1.0F);
        boolean jump = pathFollowTarget.shouldJump() || player.horizontalCollision;
        return new PursuitIntent(lookYaw, pitch, forward, strafe, jump, true);
    }

    public static PursuitIntent buildWanderIntent(Player player, long gameTime) {
        CompoundTag persistentData = player.getPersistentData();
        float yaw = persistentData.contains(WANDER_YAW_KEY) && gameTime % 28L != 0L
                ? persistentData.getFloat(WANDER_YAW_KEY)
                : player.getYRot() + (player.getRandom().nextFloat() - 0.5F) * 45.0F;
        persistentData.putFloat(WANDER_YAW_KEY, yaw);
        return new PursuitIntent(yaw, 0.0F, 0.85F, 0.0F, player.horizontalCollision, true);
    }

    public static void applyIntent(Player player, PursuitIntent intent) {
        player.setYRot(intent.yaw());
        player.setYHeadRot(intent.yaw());
        player.setYBodyRot(intent.yaw());
        player.setXRot(intent.pitch());
        player.setSprinting(intent.sprint());
        applyJumpIfNeeded(player, intent);
    }

    public static void clear(Player player) {
        PATH_STATES.remove(player.getUUID());
        player.getPersistentData().remove(WANDER_YAW_KEY);
    }

    private static boolean isValidTarget(LivingEntity entity) {
        if (shouldIgnoreTarget(entity)) {
            return false;
        }
        if (entity instanceof Player targetPlayer) {
            return !targetPlayer.isCreative() && !targetPlayer.isSpectator();
        }
        return entity instanceof Monster || entity instanceof Animal;
    }

    private static boolean shouldIgnoreTarget(LivingEntity entity) {
        return entity instanceof TamableAnimal tamableAnimal && tamableAnimal.isTame() && PsychosisTargetingConfig.ignoreTamedAnimals()
                || PsychosisTargetingConfig.ignoredEntityTypes().contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
    }

    private static int targetPriority(LivingEntity entity) {
        if (entity instanceof Player) {
            return 0;
        }
        if (entity instanceof Monster) {
            return 1;
        }
        if (entity instanceof Animal) {
            return 2;
        }
        return 3;
    }

    private static PathFollowTarget resolvePathTargetPosition(Player player, LivingEntity target, long gameTime) {
        if (!(player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            return new PathFollowTarget(player.position(), false);
        }

        PathState state = PATH_STATES.computeIfAbsent(player.getUUID(), ignored -> new PathState(new Zombie(serverLevel)));
        if (state.navigator().level() != serverLevel) {
            state = new PathState(new Zombie(serverLevel));
            PATH_STATES.put(player.getUUID(), state);
        }

        Zombie navigator = state.navigator();
        navigator.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
        navigator.setCanPickUpLoot(false);

        boolean targetChanged = !target.getUUID().equals(state.targetId());
        boolean stalePath = state.path() == null || state.path().isDone() || gameTime - state.lastRepathGameTime() >= PATH_RECALC_INTERVAL_TICKS;
        boolean stuck = state.lastProgressGameTime() > 0L && gameTime - state.lastProgressGameTime() >= PATH_STUCK_REPATH_TICKS;
        boolean collisionRepath = player.horizontalCollision
                && state.lastProgressGameTime() > 0L
                && gameTime - state.lastProgressGameTime() >= PATH_COLLISION_REPATH_TICKS;
        if (targetChanged || stalePath || stuck || collisionRepath) {
            Path path = navigator.getNavigation().createPath(target, 0);
            state.targetId(target.getUUID());
            state.path(path);
            state.lastRepathGameTime(gameTime);
            state.lastProgressPosition(player.position());
            state.lastProgressGameTime(gameTime);
        }

        Path path = state.path();
        if (path == null || !path.canReach() || path.isDone()) {
            state.path(null);
            return new PathFollowTarget(player.position(), false);
        }

        advancePath(player, navigator, path);

        updatePathProgress(state, player, gameTime);
        if (path.isDone()) {
            return new PathFollowTarget(player.position(), false);
        }

        Vec3 next = path.getNextEntityPos(navigator);
        boolean shouldJumpNow = next.y - player.getY() > 0.35D
                || player.horizontalCollision
                || state.lastProgressGameTime() > 0L && gameTime - state.lastProgressGameTime() >= 6L && next.y > player.getY() + 0.1D
                || moveRequiresStepUp(player, next);
        if (shouldJumpNow) {
            state.jumpUntilGameTime(gameTime + PATH_JUMP_HOLD_TICKS);
        }
        boolean shouldJump = state.jumpUntilGameTime() > gameTime;
        return new PathFollowTarget(next, shouldJump);
    }

    private static void advancePath(Player player, Zombie navigator, Path path) {
        Vec3 playerPosition = getPathNavigationPosition(player);
        float pathAcceptanceRadius = player.getBbWidth() > 0.75F
                ? player.getBbWidth() / 2.0F
                : 0.75F - player.getBbWidth() / 2.0F;
        Vec3 currentNodePosition = path.getNextEntityPos(navigator);
        boolean closeEnough = Math.abs(player.getX() - currentNodePosition.x) < pathAcceptanceRadius
                && Math.abs(player.getZ() - currentNodePosition.z) < pathAcceptanceRadius
                && Math.abs(player.getY() - currentNodePosition.y) < 1.0D;
        if (closeEnough || shouldAdvanceToNextNode(playerPosition, path, navigator)) {
            path.advance();
        }
    }

    private static boolean shouldAdvanceToNextNode(Vec3 playerPosition, Path path, Zombie navigator) {
        if (path.getNextNodeIndex() + 1 >= path.getNodeCount()) {
            return false;
        }

        Vec3 currentNodeCenter = Vec3.atBottomCenterOf(path.getNextNodePos());
        if (!playerPosition.closerThan(currentNodeCenter, PATH_NODE_DIRECTION_CHECK_DISTANCE)) {
            return false;
        }

        Vec3 currentNodePosition = path.getNextEntityPos(navigator);
        Vec3 nextNodePosition = path.getEntityPosAtNode(navigator, path.getNextNodeIndex() + 1);
        Vec3 toCurrent = currentNodePosition.subtract(playerPosition);
        Vec3 toNext = nextNodePosition.subtract(playerPosition);
        boolean nextIsCloser = toNext.lengthSqr() < toCurrent.lengthSqr();
        boolean currentIsVeryClose = toCurrent.length() < 0.5D;
        if (!nextIsCloser && !currentIsVeryClose) {
            return false;
        }

        return toNext.normalize().dot(toCurrent.normalize()) < 0.0D;
    }

    private static Vec3 getPathNavigationPosition(Player player) {
        return new Vec3(player.getX(), player.getY(), player.getZ());
    }

    private static TargetCandidate createTargetCandidate(Player player, LivingEntity target, LivingEntity recentAggressor) {
        boolean visible = player.hasLineOfSight(target);
        double directDistanceSqr = target.distanceToSqr(player);
        if (visible) {
            return new TargetCandidate(
                    target,
                    targetPriority(target),
                    true,
                    target == recentAggressor,
                    Math.sqrt(directDistanceSqr),
                    directDistanceSqr
            );
        }

        if (!(player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            return null;
        }

        if (directDistanceSqr > MAX_HIDDEN_TARGET_DIRECT_DISTANCE * MAX_HIDDEN_TARGET_DIRECT_DISTANCE) {
            return null;
        }

        Zombie navigator = new Zombie(serverLevel);
        navigator.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
        Path path = navigator.getNavigation().createPath(target, 0);
        if (path == null || !path.canReach() || path.isDone()) {
            return null;
        }

        double pathDistance = computePathDistance(navigator, path, player.position());
        if (pathDistance > MAX_HIDDEN_TARGET_PATH_DISTANCE) {
            return null;
        }

        return new TargetCandidate(
                target,
                targetPriority(target),
                false,
                target == recentAggressor,
                pathDistance,
                directDistanceSqr
        );
    }

    private static LivingEntity selectVisibleTarget(List<TargetCandidate> candidates) {
        return candidates.stream()
                .filter(TargetCandidate::visible)
                .sorted(Comparator
                        .comparingInt((TargetCandidate candidate) -> candidate.directDistanceSqr() <= IMMEDIATE_VISIBLE_TARGET_RANGE * IMMEDIATE_VISIBLE_TARGET_RANGE ? 0 : 1)
                        .thenComparingDouble(TargetCandidate::directDistanceSqr)
                        .thenComparingInt(candidate -> candidate.recentAggressor() ? 0 : 1))
                .map(TargetCandidate::entity)
                .findFirst()
                .orElse(null);
    }

    private static PursuitIntent buildDirectHuntIntent(Player player, LivingEntity target) {
        Vec3 targetPosition = target.position().add(0.0D, target.getBbHeight() * 0.65D, 0.0D);
        Vec3 lookDelta = targetPosition.subtract(player.getEyePosition());
        Vec3 moveDelta = target.position().subtract(player.position());
        Vec3 moveHorizontal = flatten(moveDelta);
        if (moveHorizontal.lengthSqr() < 0.0001D) {
            return new PursuitIntent(player.getYRot(), player.getXRot(), 0.0F, 0.0F, false, true);
        }

        Vec3 moveDirection = moveHorizontal.normalize();
        float yaw = (float) (Math.toDegrees(Math.atan2(moveDirection.z, moveDirection.x)) - 90.0D);
        float pitch = (float) Mth.clamp(-Math.toDegrees(Math.atan2(lookDelta.y, Math.max(0.001D, moveHorizontal.length()))), -35.0D, 35.0D);
        boolean shouldJump = player.horizontalCollision
                || target.getY() > player.getY() + 0.6D && moveHorizontal.lengthSqr() < 6.25D;
        return new PursuitIntent(yaw, pitch, 1.0F, 0.0F, shouldJump, true);
    }

    private static double computePathDistance(Zombie navigator, Path path, Vec3 startPosition) {
        int nodeCount = path.getNodeCount();
        if (nodeCount <= 0) {
            return Double.MAX_VALUE;
        }

        double distance = 0.0D;
        Vec3 previous = startPosition;
        for (int nodeIndex = 0; nodeIndex < nodeCount; nodeIndex++) {
            Vec3 nodePosition = path.getEntityPosAtNode(navigator, nodeIndex);
            distance += previous.distanceTo(nodePosition);
            previous = nodePosition;
        }
        return distance;
    }

    private static void updatePathProgress(PathState state, Player player, long gameTime) {
        Vec3 position = player.position();
        Vec3 lastProgressPosition = state.lastProgressPosition();
        if (lastProgressPosition == null || position.distanceToSqr(lastProgressPosition) >= PATH_PROGRESS_DISTANCE_SQR) {
            state.lastProgressPosition(position);
            state.lastProgressGameTime(gameTime);
        }
    }

    private static boolean moveRequiresStepUp(Player player, Vec3 next) {
        Vec3 movementDelta = next.subtract(player.position());
        Vec3 horizontal = flatten(movementDelta);
        if (horizontal.lengthSqr() < 0.04D) {
            return false;
        }
        return next.y > player.getY() + 0.1D && horizontal.length() < 1.6D;
    }

    private static void applyJumpIfNeeded(Player player, PursuitIntent intent) {
        if (!intent.jump()
                || !player.onGround()
                || player.onClimbable()
                || player.isInWaterOrBubble()) {
            return;
        }

        player.jumpFromGround();
        player.hasImpulse = true;
        player.hurtMarked = true;
        player.fallDistance = 0.0F;
        PlayerMotionSyncHelper.sync(player);
    }

    private static Vec3 flatten(Vec3 vector) {
        return new Vec3(vector.x, 0.0D, vector.z);
    }

    public record PursuitIntent(float yaw, float pitch, float forward, float strafe, boolean jump, boolean sprint) {
    }

    private record PathFollowTarget(Vec3 position, boolean shouldJump) {
    }

    private record TargetCandidate(
            LivingEntity entity,
            int priority,
            boolean visible,
            boolean recentAggressor,
            double pathDistance,
            double directDistanceSqr
    ) {
    }

    private static final class PathState {
        private final Zombie navigator;
        private UUID targetId;
        private Path path;
        private long lastRepathGameTime;
        private Vec3 lastProgressPosition;
        private long lastProgressGameTime;
        private long jumpUntilGameTime;

        private PathState(Zombie navigator) {
            this.navigator = navigator;
        }

        private Zombie navigator() {
            return navigator;
        }

        private UUID targetId() {
            return targetId;
        }

        private void targetId(UUID targetId) {
            this.targetId = targetId;
        }

        private Path path() {
            return path;
        }

        private void path(Path path) {
            this.path = path;
        }

        private long lastRepathGameTime() {
            return lastRepathGameTime;
        }

        private void lastRepathGameTime(long lastRepathGameTime) {
            this.lastRepathGameTime = lastRepathGameTime;
        }

        private Vec3 lastProgressPosition() {
            return lastProgressPosition;
        }

        private void lastProgressPosition(Vec3 lastProgressPosition) {
            this.lastProgressPosition = lastProgressPosition;
        }

        private long lastProgressGameTime() {
            return lastProgressGameTime;
        }

        private void lastProgressGameTime(long lastProgressGameTime) {
            this.lastProgressGameTime = lastProgressGameTime;
        }

        private long jumpUntilGameTime() {
            return jumpUntilGameTime;
        }

        private void jumpUntilGameTime(long jumpUntilGameTime) {
            this.jumpUntilGameTime = jumpUntilGameTime;
        }
    }
}
