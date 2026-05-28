package de.artemis.cyberneticenhancements.common.registry;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.JukeboxSong;

public final class ModJukeboxSongs {
    public static final ResourceKey<JukeboxSong> WHOS_READY_FOR_TOMORROW_INSTRUMENTAL =
            ResourceKey.create(
                    Registries.JUKEBOX_SONG,
                    ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "whos_ready_for_tomorrow_instrumental")
            );

    private ModJukeboxSongs() {
    }
}
