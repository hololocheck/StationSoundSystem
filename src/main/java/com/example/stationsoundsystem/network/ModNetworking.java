package com.example.stationsoundsystem.network;

import com.example.stationsoundsystem.StationSoundSystem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = StationSoundSystem.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModNetworking {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(StationSoundSystem.MOD_ID).versioned("1.0");

        registrar.playToServer(
                AudioUploadPayload.TYPE,
                AudioUploadPayload.STREAM_CODEC,
                AudioUploadPayload::handle
        );

        registrar.playToServer(
                StartRecordingPayload.TYPE,
                StartRecordingPayload.STREAM_CODEC,
                StartRecordingPayload::handle
        );

        registrar.playToServer(
                ToggleRangeDisplayPayload.TYPE,
                ToggleRangeDisplayPayload.STREAM_CODEC,
                ToggleRangeDisplayPayload::handle
        );

        registrar.playToServer(
                PlaybackControlPayload.TYPE,
                PlaybackControlPayload.STREAM_CODEC,
                PlaybackControlPayload::handle
        );

        registrar.playToServer(
                ClearAudioPayload.TYPE,
                ClearAudioPayload.STREAM_CODEC,
                ClearAudioPayload::handle
        );

        registrar.playToServer(
                ToggleAttenuationPayload.TYPE,
                ToggleAttenuationPayload.STREAM_CODEC,
                ToggleAttenuationPayload::handle
        );

        registrar.playToServer(
                SetAttenuationRangePayload.TYPE,
                SetAttenuationRangePayload.STREAM_CODEC,
                SetAttenuationRangePayload::handle
        );

        registrar.playToServer(
                SetRangeBoardDataPayload.TYPE,
                SetRangeBoardDataPayload.STREAM_CODEC,
                SetRangeBoardDataPayload::handle
        );

        registrar.playToClient(
                ClientPlayAudioPayload.TYPE,
                ClientPlayAudioPayload.STREAM_CODEC,
                ClientPlayAudioPayload::handle
        );

        registrar.playToClient(
                ClientStopAudioPayload.TYPE,
                ClientStopAudioPayload.STREAM_CODEC,
                ClientStopAudioPayload::handle
        );

        registrar.playToClient(
                ClientNotifyPayload.TYPE,
                ClientNotifyPayload.STREAM_CODEC,
                ClientNotifyPayload::handle
        );
    }
}
