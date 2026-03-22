package com.example.stationsoundsystem.network;

import com.example.stationsoundsystem.StationSoundSystem;
import com.example.stationsoundsystem.audio.AudioManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server → Client: begin audio playback on each client independently.
 * Audio data is included so every client can decode and play locally.
 */
public record ClientPlayAudioPayload(
        BlockPos pos,
        byte[] audioData,
        String format,
        BlockPos rangePos1,
        BlockPos rangePos2,
        boolean attenuationMode,
        int[] attenuationRanges) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientPlayAudioPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(StationSoundSystem.MOD_ID, "client_play_audio"));

    public static final StreamCodec<FriendlyByteBuf, ClientPlayAudioPayload> STREAM_CODEC =
            StreamCodec.of(ClientPlayAudioPayload::write, ClientPlayAudioPayload::read);

    private static void write(FriendlyByteBuf buf, ClientPlayAudioPayload p) {
        buf.writeBlockPos(p.pos);
        buf.writeByteArray(p.audioData);
        buf.writeUtf(p.format);
        buf.writeBoolean(p.rangePos1 != null);
        if (p.rangePos1 != null) {
            buf.writeBlockPos(p.rangePos1);
            buf.writeBlockPos(p.rangePos2);
        }
        buf.writeBoolean(p.attenuationMode);
        buf.writeVarInt(p.attenuationRanges.length);
        for (int r : p.attenuationRanges) buf.writeVarInt(r);
    }

    private static ClientPlayAudioPayload read(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        byte[] audioData = buf.readByteArray(10 * 1024 * 1024); // max 10 MB
        String format = buf.readUtf();
        boolean hasRange = buf.readBoolean();
        BlockPos rangePos1 = hasRange ? buf.readBlockPos() : null;
        BlockPos rangePos2 = hasRange ? buf.readBlockPos() : null;
        boolean attenuationMode = buf.readBoolean();
        int len = buf.readVarInt();
        int[] attenuationRanges = new int[len];
        for (int i = 0; i < len; i++) attenuationRanges[i] = buf.readVarInt();
        return new ClientPlayAudioPayload(pos, audioData, format, rangePos1, rangePos2,
                attenuationMode, attenuationRanges);
    }

    public static void handle(ClientPlayAudioPayload payload, IPayloadContext context) {
        // Use context.player().level() (returns Level, not ClientLevel) to avoid
        // loading net.minecraft.client.* classes on the dedicated server.
        context.enqueueWork(() ->
                AudioManager.getInstance().playAudio(
                        context.player().level(), payload.pos, payload.audioData, payload.format,
                        payload.rangePos1, payload.rangePos2,
                        payload.attenuationMode, payload.attenuationRanges));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
