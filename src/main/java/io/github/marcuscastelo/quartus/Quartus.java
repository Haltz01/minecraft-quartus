package io.github.marcuscastelo.quartus;

import io.github.marcuscastelo.quartus.blockentity.ImplementedInventory;
import io.github.marcuscastelo.quartus.gui.CompilerBlockController;
import io.github.marcuscastelo.quartus.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
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
        QuartusNetworkHandlers.init();
        QuartusCottonControllers.init();



        LOGGER.info("[Quartus] Server ready!");
    }

    public static Identifier id(String name) {
        return new Identifier(MOD_ID, name);
    }
}
