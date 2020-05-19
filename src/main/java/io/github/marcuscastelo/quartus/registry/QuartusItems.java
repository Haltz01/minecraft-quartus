package io.github.marcuscastelo.quartus.registry;

import io.github.marcuscastelo.quartus.Quartus;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

public class QuartusItems {
    public static final BlockItem WIRE;
    public static final BlockItem EXTENSOR_GATE;
    public static final BlockItem DISTRIBUTOR_GATE;
    public static final BlockItem AND_GATE;
    public static final BlockItem OR_GATE;
    public static final BlockItem XOR_GATE;
    public static final BlockItem NOT_GATE;
    public static final BlockItem NAND_GATE;
    public static final BlockItem NOR_GATE;
    public static final BlockItem COMPILER;
    public static final BlockItem INPUT;

    public static final Item FLOPPY_DISK;

    public static void init() {}

    private static BlockItem register(String item_name, Block block) {
        return Registry.register(Registry.ITEM, Quartus.id(item_name), new BlockItem(block, new Item.Settings().group(Quartus.ITEMGROUP)));
    }

    private static Item register(String item_name, Item item) {
        return Registry.register(Registry.ITEM, Quartus.id(item_name), item);
    }

    static {
        WIRE = register("wire", QuartusBlocks.WIRE);
        EXTENSOR_GATE = register("extensor_gate", QuartusBlocks.EXTENSOR_GATE);
        DISTRIBUTOR_GATE = register("distributor_gate", QuartusBlocks.DISTRIBUTOR_GATE);

        AND_GATE = register("and_gate", QuartusBlocks.AND_GATE);
        OR_GATE = register("or_gate", QuartusBlocks.OR_GATE);
        XOR_GATE = NOR_GATE = NOT_GATE = NAND_GATE = null;

        COMPILER = register("compiler", QuartusBlocks.COMPILER);
        INPUT = register("input", QuartusBlocks.INPUT);

        FLOPPY_DISK = register("floppy_disk", new Item(new Item.Settings().group(Quartus.ITEMGROUP)));

    }
}
