package de.artemis.cyberneticenhancements.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public final class FixerEntity extends AbstractCityNpcEntity {
    public FixerEntity(EntityType<? extends AbstractCityNpcEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public NpcType npcType() {
        return NpcType.FIXER;
    }
}
