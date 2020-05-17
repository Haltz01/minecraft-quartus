package io.github.geeleonidas.withery;

import io.github.geeleonidas.withery.block.circuit_components.AndGateBlock;
import io.github.geeleonidas.withery.block.circuit_components.OrGateBlock;
import io.github.geeleonidas.withery.block.circuit_components.WireBlock;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Withery implements ModInitializer {
    public static final String MOD_ID = "withery";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static Withery INSTANCE;

    public static Block andGateBlock, orGateBlock, wireBlock;

    public static ItemGroup ITEMGROUP = FabricItemGroupBuilder.build(
            id("item_group"), () -> new ItemStack(Items.WITHER_ROSE));

    @Override
    public void onInitialize() {
        INSTANCE = this;

        Registry.register(Registry.BLOCK, id("and_gate"), andGateBlock);
        Registry.register(Registry.ITEM, id("and_gate"), new BlockItem(andGateBlock, new Item.Settings().group(ItemGroup.MISC)));

        Registry.register(Registry.BLOCK, id("or_gate"), orGateBlock);
        Registry.register(Registry.ITEM, id("or_gate"), new BlockItem(orGateBlock, new Item.Settings().group(ItemGroup.MISC)));

        Registry.register(Registry.BLOCK, id("wire"), wireBlock);
        Registry.register(Registry.ITEM, id("wire"), new BlockItem(wireBlock, new Item.Settings().group(ItemGroup.MISC)));

        LOGGER.info("[Withery] Server withered away!");
    }

    public static Identifier id(String name) {
        return new Identifier(MOD_ID, name);
    }

    static {
        andGateBlock = new AndGateBlock();
        orGateBlock = new OrGateBlock();
        wireBlock = new WireBlock();
    }
}
