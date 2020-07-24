package io.github.marcuscastelo.quartus.registry;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.blockentity.CompilerBlockEntity;
import io.github.marcuscastelo.quartus.blockentity.ExecutorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

/**
 * Classe responsável por registrar as Block Entities do Compilador e do Executor no jogo
 */
public class QuartusBlockEntities {
    // Tipo de BlockEntity do compilador
    public static BlockEntityType<CompilerBlockEntity> COMPILER_BLOCK_ENTITY_TYPE;
    // Tipo de BlockEntity do executor
    public static BlockEntityType<ExecutorBlockEntity> EXECUTOR_BLOCK_ENTITY_TYPE;

    public static void init() {}

    /**
     * Método utilizado para registrar um tipo de BlockEntity atrelado a determinado bloco (de acordo com a Wiki do FabricMC)
     * @param blockEntityName               Nome da Block Entity a ser registrada
     * @param blockEntitySupplier           Construtor da BlockEntity
     * @param attachedBlock                 Bloco ao qual a BlockEntity ficará atrelada
     * @param <T>                           Classe genérica que herda a BlockEntity (classe filha de Block Entity)
     * @return                              Tipo de BlockEntity registrada
     */
    private static <T extends BlockEntity> BlockEntityType<T> register(String blockEntityName, Supplier<T> blockEntitySupplier, Block attachedBlock) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, Quartus.id(blockEntityName), BlockEntityType.Builder.create(blockEntitySupplier, attachedBlock).build(null));
    }

    static {
        COMPILER_BLOCK_ENTITY_TYPE = register("compiler", CompilerBlockEntity::new, QuartusBlocks.COMPILER);
        EXECUTOR_BLOCK_ENTITY_TYPE = register("executor", ExecutorBlockEntity::new, QuartusBlocks.EXECUTOR);
    }
}
