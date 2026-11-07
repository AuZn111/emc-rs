package com.wintercogs.emcrs.logic;

import com.wintercogs.emcrs.config.EmcConfig;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;

/**
 * Progression gate: the network EMC shop is locked until the owner has ever
 * obtained a ProjectE Transmutation Table or Transmutation Tablet.
 *
 * <p>The "obtained" flag is stored in the player's Forge persistent data, so
 * placing the table or losing the tablet does not re-lock the player. The flag
 * is also set when a transmutation table block is placed, and detected from
 * the player's inventory or a nearby placed table.</p>
 */
public final class TransmutationGate {
    private static final String UNLOCKED_KEY = "emc_rs_transmutation_unlocked";
    private static final int NEARBY_RADIUS = 8;

    private TransmutationGate() {
    }

    public static boolean canUseEmc(ServerPlayer player) {
        if (player == null) {
            return false;
        }
        if (!EmcConfig.REQUIRE_TRANSMUTATION_TABLE.get()) {
            return true;
        }
        if (player.getPersistentData().getBoolean(UNLOCKED_KEY)) {
            return true;
        }
        if (hasTableInInventory(player) || hasTableNearby(player) || hasTableAdvancement(player)) {
            unlock(player);
            return true;
        }
        return false;
    }

    public static boolean canConvertWithoutTable() {
        return !EmcConfig.REQUIRE_TRANSMUTATION_TABLE.get()
                || EmcConfig.CONVERT_WITHOUT_TABLE.get();
    }

    public static void unlock(ServerPlayer player) {
        player.getPersistentData().putBoolean(UNLOCKED_KEY, true);
    }

    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(event.getPlacedBlock().getBlock());
        if (id != null && "projecte:transmutation_table".equals(id.toString())) {
            unlock(player);
        }
    }

    private static boolean hasTableInInventory(ServerPlayer player) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (!stack.isEmpty() && isTableItem(stack)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasTableNearby(ServerPlayer player) {
        BlockPos center = player.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-NEARBY_RADIUS, -4, -NEARBY_RADIUS),
                center.offset(NEARBY_RADIUS, 4, NEARBY_RADIUS))) {
            BlockState state = player.level().getBlockState(pos);
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
            if (id != null && "projecte:transmutation_table".equals(id.toString())) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasTableAdvancement(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return false;
        }
        return isAdvancementDone(server, player, "transmutation_table")
                || isAdvancementDone(server, player, "transmutation_tablet");
    }

    private static boolean isAdvancementDone(MinecraftServer server, ServerPlayer player, String name) {
        Advancement holder = server.getAdvancements()
                .getAdvancement(new ResourceLocation("projecte", name));
        return holder != null && player.getAdvancements().getOrStartProgress(holder).isDone();
    }

    private static boolean isTableItem(ItemStack stack) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id == null) {
            return false;
        }
        String name = id.toString();
        return "projecte:transmutation_table".equals(name)
                || "projecte:transmutation_tablet".equals(name)
                || "projecte:portable_transmutation_table".equals(name);
    }
}
