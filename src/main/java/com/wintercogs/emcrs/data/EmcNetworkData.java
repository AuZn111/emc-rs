package com.wintercogs.emcrs.data;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Per-level persistent state for the blockless EMC networks. The key is the RS
 * controller position; the value is the player who supplies the ProjectE
 * knowledge/EMC for that network.
 */
public class EmcNetworkData extends SavedData {
    private static final String DATA_NAME = "emc_rs_networks";
    private static final String NBT_OWNERS = "Owners";
    private static final String NBT_POS = "Pos";
    private static final String NBT_OWNER = "Owner";

    private final Map<Long, UUID> owners = new HashMap<>();

    public static EmcNetworkData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                EmcNetworkData::load, EmcNetworkData::new, DATA_NAME);
    }

    public static EmcNetworkData load(CompoundTag tag) {
        EmcNetworkData data = new EmcNetworkData();
        ListTag ownersTag = tag.getList(NBT_OWNERS, 10);
        for (int i = 0; i < ownersTag.size(); i++) {
            CompoundTag entry = ownersTag.getCompound(i);
            if (entry.hasUUID(NBT_OWNER)) {
                data.owners.put(entry.getLong(NBT_POS), entry.getUUID(NBT_OWNER));
            }
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag ownersTag = new ListTag();
        this.owners.forEach((pos, owner) -> {
            CompoundTag entry = new CompoundTag();
            entry.putLong(NBT_POS, pos);
            entry.putUUID(NBT_OWNER, owner);
            ownersTag.add(entry);
        });
        tag.put(NBT_OWNERS, ownersTag);
        return tag;
    }

    @Nullable
    public UUID getOwner(BlockPos controllerPos) {
        return this.owners.get(controllerPos.asLong());
    }

    public void setOwner(BlockPos controllerPos, UUID owner) {
        this.owners.put(controllerPos.asLong(), owner);
        setDirty();
    }

    public void clear(BlockPos controllerPos) {
        this.owners.remove(controllerPos.asLong());
        setDirty();
    }
}
