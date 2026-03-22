package com.example.stationsoundsystem;

import com.example.stationsoundsystem.block.ModBlocks;
import com.example.stationsoundsystem.blockentity.ModBlockEntities;
import com.example.stationsoundsystem.creative.ModCreativeTabs;
import com.example.stationsoundsystem.item.ModDataComponents;
import com.example.stationsoundsystem.item.ModItems;
import com.example.stationsoundsystem.menu.ModMenuTypes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(StationSoundSystem.MOD_ID)
public class StationSoundSystem {
    public static final String MOD_ID = "stationsoundsystem";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public StationSoundSystem(IEventBus modEventBus) {
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModDataComponents.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("StationSoundSystem initialized");
    }
}
