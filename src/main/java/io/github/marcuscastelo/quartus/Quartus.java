package io.github.marcuscastelo.quartus;

import io.github.marcuscastelo.quartus.item.FloppyDiskItem;
import io.github.marcuscastelo.quartus.registry.QuartusBlockEntities;
import io.github.marcuscastelo.quartus.registry.QuartusBlocks;
import io.github.marcuscastelo.quartus.registry.QuartusCottonGUIs;
import io.github.marcuscastelo.quartus.registry.QuartusItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.SynchronizeTagsS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Quartus implements ModInitializer {
    public static final String MOD_ID = "quartus";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static Quartus INSTANCE;

    public static ItemGroup ITEMGROUP = FabricItemGroupBuilder.build(
            id("item_group"), () -> new ItemStack(Items.WITHER_ROSE));

    @Override
    public void onInitialize() {
        System.out.println("onInitialize()");
        INSTANCE = this;

        QuartusBlocks.init();
        QuartusItems.init();
        QuartusBlockEntities.init();

        ServerSidePacketRegistry.INSTANCE.register(Quartus.id("foda"), (packetContext, packetByteBuf) -> {
            BlockPos blockPos = packetByteBuf.readBlockPos();
            CompoundTag compoundTag = packetByteBuf.readCompoundTag();

            packetContext.getTaskQueue().execute(() -> {
                World world = packetContext.getPlayer().world;
                Inventory inv = QuartusCottonGUIs.getBlockInventory(world, blockPos);

                assert inv != null;
                ItemStack itemStack = inv.getInvStack(0);
                if (itemStack.isEmpty()) return;
                if (!itemStack.getItem().equals(QuartusItems.FLOPPY_DISK)) return;
                if (itemStack.getCount() != 1) return;

                itemStack.setTag(compoundTag);

            });
        });

        LOGGER.info("[Withery] Server withered away!");
    }

    public static Identifier id(String name) {
        return new Identifier(MOD_ID, name);
    }
}
