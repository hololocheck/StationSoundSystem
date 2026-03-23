package com.example.stationsoundsystem.network;

import com.example.stationsoundsystem.StationSoundSystem;
import com.example.stationsoundsystem.audio.AudioStorage;
import com.example.stationsoundsystem.blockentity.PlaybackDeviceBlockEntity;
import com.example.stationsoundsystem.item.ModDataComponents;
import com.example.stationsoundsystem.item.RangeBoardItem;
import com.example.stationsoundsystem.item.RecordingMediumItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PlaybackControlPayload(BlockPos pos, boolean play) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PlaybackControlPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(StationSoundSystem.MOD_ID, "playback_control"));

    public static final StreamCodec<FriendlyByteBuf, PlaybackControlPayload> STREAM_CODEC =
            StreamCodec.of(PlaybackControlPayload::write, PlaybackControlPayload::read);

    private static void write(FriendlyByteBuf buf, PlaybackControlPayload payload) {
        buf.writeBlockPos(payload.pos);
        buf.writeBoolean(payload.play);
    }

    private static PlaybackControlPayload read(FriendlyByteBuf buf) {
        return new PlaybackControlPayload(buf.readBlockPos(), buf.readBoolean());
    }

    public static void handle(PlaybackControlPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            var level = player.level();
            BlockEntity entity = level.getBlockEntity(payload.pos);
            if (!(entity instanceof PlaybackDeviceBlockEntity be)) return;

            if (payload.play) {
                ItemStack mediaStack = be.getInventory().getStackInSlot(PlaybackDeviceBlockEntity.MEDIA_SLOT);
                if (!RecordingMediumItem.hasAudioData(mediaStack)) return;

                if (!(level instanceof ServerLevel sl)) return;
                byte[] audioData = AudioStorage.loadForItem(sl.getServer(), mediaStack);
                if (audioData == null) return;
                String format = mediaStack.getOrDefault(ModDataComponents.AUDIO_FORMAT, "ogg");

                BlockPos rangePos1 = null, rangePos2 = null;
                ItemStack rangeStack = be.getInventory().getStackInSlot(PlaybackDeviceBlockEntity.RANGE_SLOT);
                if (RangeBoardItem.hasRange(rangeStack)) {
                    rangePos1 = rangeStack.get(ModDataComponents.RANGE_POS1);
                    rangePos2 = rangeStack.get(ModDataComponents.RANGE_POS2);
                }

                int[] attRanges = ModDataComponents.getAttenuationRangesArray(rangeStack);
                if (!rangeStack.has(ModDataComponents.ATTENUATION_RANGES)) {
                    java.util.Arrays.fill(attRanges, be.getAttenuationRange());
                }

                be.setIsPlaying(true);

                ClientPlayAudioPayload metaPayload = new ClientPlayAudioPayload(
                        payload.pos, audioData.length, format, rangePos1, rangePos2,
                        be.isAttenuationMode(), attRanges);

                for (ServerPlayer sp : sl.getServer().getPlayerList().getPlayers()) {
                    PacketDistributor.sendToPlayer(sp, metaPayload);
                    ClientAudioChunkPayload.sendChunked(sp, payload.pos, audioData);
                }

            } else {
                be.setIsPlaying(false);

                ClientStopAudioPayload clientPayload = new ClientStopAudioPayload(payload.pos);
                if (level instanceof ServerLevel sl2) {
                    for (ServerPlayer sp : sl2.getServer().getPlayerList().getPlayers()) {
                        PacketDistributor.sendToPlayer(sp, clientPayload);
                    }
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
