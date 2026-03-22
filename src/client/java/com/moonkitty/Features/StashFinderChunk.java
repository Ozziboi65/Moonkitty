package com.moonkitty.Features;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.List;

public class StashFinderChunk {

    public record Entry(BlockPos pos, BlockEntityType<?> type) {
    }

    public final ChunkPos CHUNK_POS;
    public final List<Entry> BLOCK_ENTITY_LIST = new ArrayList<>();

    public StashFinderChunk(ChunkPos chunkPos) {
        this.CHUNK_POS = chunkPos;
    }

    public void addBlockEntity(BlockEntity blockEntity) {
        BLOCK_ENTITY_LIST.add(new Entry(blockEntity.getPos(), blockEntity.getType()));
    }

}