package de.artemis.cyberneticenhancements.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public final class MercEntity extends AbstractCityNpcEntity {
    public MercEntity(EntityType<? extends AbstractCityNpcEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public NpcType npcType() {
        return NpcType.MERC;
    }
}
