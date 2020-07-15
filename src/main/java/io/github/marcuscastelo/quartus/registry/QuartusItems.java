package io.github.marcuscastelo.quartus.registry;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.item.FloppyDiskItem;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class QuartusItems {
    public static final BlockItem WIRE;
    public static final BlockItem EXTENSOR_GATE;
    public static final BlockItem DISTRIBUTOR_GATE;

    public static final BlockItem AND_GATE;
    public static final BlockItem NAND_GATE;
    public static final BlockItem OR_GATE;
    public static final BlockItem NOR_GATE;
    public static final BlockItem XOR_GATE;
    public static final BlockItem XNOR_GATE;
    public static final BlockItem NOT_GATE;

    public static final BlockItem MULTIPLEXER_GATE;

    public static final BlockItem COMPILER;
    public static final BlockItem EXECUTOR;
    public static final BlockItem EXTENSOR_IO;

    public static final BlockItem INPUT;
    public static final BlockItem OUTPUT;

    public static final Item FLOPPY_DISK;


    private static final List<ItemStack> sampleItemStacksInItemGroupOrder = new ArrayList<>();
    private static BlockItem register(String item_name, Block block) {
        return register(item_name, new BlockItem(block, new Item.Settings().group(Quartus.ITEMGROUP)));
    }

    private static <T extends Item> T register(String item_name, T item) {
        sampleItemStacksInItemGroupOrder.add(new ItemStack(item));
        return Registry.register(Registry.ITEM, Quartus.id(item_name), item);
    }

    public static void appendItemGroupStacksInorder(List<ItemStack> itemGroupStacks) {
        itemGroupStacks.addAll(sampleItemStacksInItemGroupOrder);
    }

    static {
        WIRE = register("wire", QuartusBlocks.WIRE);
        EXTENSOR_GATE = register("extensor_gate", QuartusBlocks.EXTENSOR_GATE);
        DISTRIBUTOR_GATE = register("distributor_gate", QuartusBlocks.DISTRIBUTOR_GATE);

        AND_GATE = register("and_gate", QuartusBlocks.AND_GATE);
        NAND_GATE = register("nand_gate", QuartusBlocks.NAND_GATE);
        OR_GATE = register("or_gate", QuartusBlocks.OR_GATE);
        NOR_GATE = register("nor_gate", QuartusBlocks.NOR_GATE);
        XOR_GATE = register("xor_gate", QuartusBlocks.XOR_GATE);
        XNOR_GATE = register("xnor_gate", QuartusBlocks.XNOR_GATE);
        NOT_GATE = register("not_gate", QuartusBlocks.NOT_GATE);

        COMPILER = register("compiler", QuartusBlocks.COMPILER);
        EXECUTOR = register("executor", QuartusBlocks.EXECUTOR);
        EXTENSOR_IO = register("extensor_io", QuartusBlocks.EXTENSOR_IO);

        MULTIPLEXER_GATE = register("multiplexer", QuartusBlocks.MULTIPLEXER_GATE);

        INPUT = register("input", QuartusBlocks.INPUT);
        OUTPUT = register("output", QuartusBlocks.OUTPUT);

        FLOPPY_DISK = register("floppy_disk", new FloppyDiskItem());

    }
    public static void init() {}
}
