package io.github.marcuscastelo.quartus.registry;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.blockentity.CompilerBlockEntity;
import io.github.marcuscastelo.quartus.blockentity.ExecutorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public class QuartusBlockEntities {
    public static BlockEntityType<CompilerBlockEntity> COMPILER_BLOCK_ENTITY_TYPE;
    public static BlockEntityType<ExecutorBlockEntity> EXECUTOR_BLOCK_ENTITY_TYPE;

    public static void init() {}

    private static <T extends BlockEntity> BlockEntityType<T> register(String be_name, Supplier<T> blockEntitySupplier, Block attachedBlock) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, Quartus.id(be_name), BlockEntityType.Builder.create(blockEntitySupplier, attachedBlock).build(null));
    }

    static {
        COMPILER_BLOCK_ENTITY_TYPE = register("compiler", CompilerBlockEntity::new, QuartusBlocks.COMPILER);
        EXECUTOR_BLOCK_ENTITY_TYPE = register("executor", ExecutorBlockEntity::new, QuartusBlocks.EXECUTOR);
    }
}
