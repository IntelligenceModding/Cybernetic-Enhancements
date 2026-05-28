package de.artemis.cyberneticenhancements.common.registry;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, CyberneticEnhancements.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_WHOS_READY_FOR_TOMORROW_INSTRUMENTAL =
            SOUND_EVENTS.register(
                    "music_disc.whos_ready_for_tomorrow_instrumental",
                    () -> SoundEvent.createVariableRangeEvent(id("music_disc.whos_ready_for_tomorrow_instrumental"))
            );

    private ModSoundEvents() {
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, path);
    }
}
