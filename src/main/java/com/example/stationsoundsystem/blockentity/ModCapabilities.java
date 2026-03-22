package com.example.stationsoundsystem.blockentity;

import com.example.stationsoundsystem.StationSoundSystem;
import com.example.stationsoundsystem.block.ModBlocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = StationSoundSystem.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModCapabilities {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Playback device - expose inventory for item conduits
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.PLAYBACK_DEVICE.get(),
                (blockEntity, direction) -> blockEntity.getInventory()
        );

        // Recording device - expose inventory for item conduits
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.RECORDING_DEVICE.get(),
                (blockEntity, direction) -> blockEntity.getInventory()
        );
    }
}
