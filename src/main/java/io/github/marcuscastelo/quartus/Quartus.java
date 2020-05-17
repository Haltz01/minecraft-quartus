package io.github.marcuscastelo.quartus;

import io.github.marcuscastelo.quartus.block.circuit_components.AndGateBlock;
import io.github.marcuscastelo.quartus.block.circuit_components.OrGateBlock;
import io.github.marcuscastelo.quartus.block.circuit_components.WireBlock;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Quartus implements ModInitializer {
    public static final String MOD_ID = "quartus";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static Quartus INSTANCE;

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
