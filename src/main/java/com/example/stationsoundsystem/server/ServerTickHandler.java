package com.example.stationsoundsystem.server;

import com.example.stationsoundsystem.StationSoundSystem;
import com.example.stationsoundsystem.audio.AudioStorage;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Server-side tick handler for periodic orphaned audio file cleanup.
 * Runs cleanup every 5 minutes (6000 ticks).
 */
@EventBusSubscriber(modid = StationSoundSystem.MOD_ID)
public class ServerTickHandler {

    private static final int CLEANUP_INTERVAL_TICKS = 6000; // 5 minutes
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        tickCounter++;
        if (tickCounter < CLEANUP_INTERVAL_TICKS) return;
        tickCounter = 0;

        MinecraftServer server = event.getServer();
        try {
            var referencedIds = AudioStorage.collectReferencedIds(server);
            AudioStorage.cleanupOrphans(server, referencedIds);
        } catch (Exception e) {
            StationSoundSystem.LOGGER.error("Error during audio cleanup", e);
        }
    }
}
