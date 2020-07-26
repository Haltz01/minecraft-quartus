package io.github.marcuscastelo.quartus.blockentity;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.block.ExecutorIOBlock;
import io.github.marcuscastelo.quartus.circuit.CircuitDescriptor;
import io.github.marcuscastelo.quartus.circuit.CircuitExecutor;
import io.github.marcuscastelo.quartus.circuit.components.info.ComponentDirectionInfo;
import io.github.marcuscastelo.quartus.circuit.components.info.ComponentExecutionInfo;
import io.github.marcuscastelo.quartus.registry.QuartusBlockEntities;
import io.github.marcuscastelo.quartus.registry.QuartusBlocks;
import io.github.marcuscastelo.quartus.registry.QuartusItems;
import jdk.internal.jline.internal.Nullable;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe que define o BlockEntity do Executor
 */
public class ExecutorBlockEntity extends BlockEntity implements ImplementedInventory, BlockEntityClientSerializable {
	//Variável que define o tick/clock do executor
    public static final int EXECUTION_DELAY = 2; // cada tick equivale a 1/20 segundos

	//Variáveis auxiliares
    boolean executing = false;
    boolean loadedFromSave = false;
    CircuitDescriptor circuitDescriptor = null;
    CircuitExecutor circuitExecutor = null;
    DefaultedList<ItemStack> inventoryItems;
    String errorMessage = "blockentity.quartus.executor.unknown_error";

    String circuitDescStr = "", circuitStateStr = "";

    List<BlockPos> inputsPos = new ArrayList<>();
    List<BlockPos> outputsPos = new ArrayList<>();

	/**
	 * Construtor padrão da classe ExecutorBlockEntity
	 */
    public ExecutorBlockEntity() {
        super(QuartusBlockEntities.EXECUTOR_BLOCK_ENTITY_TYPE);
        this.inventoryItems = DefaultedList.ofSize(1, ItemStack.EMPTY);
    }

	/**
	 * Método que setta itens ao BlockEntity
	 */
    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventoryItems;
    }

	/**
	 * Método que atribui itens a uma tag e retorna os dados
	 * @param tag		Tag a ser atribuída
	 * @return		Tag/dados armazenados
	 */
    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if (circuitDescriptor != null) {
            tag.putString("circuit_description", circuitDescriptor.serialize());
            System.out.println("D Salvando: " + circuitDescriptor.serialize());
        }
        if (circuitExecutor != null) {
            tag.putString("execution_state", circuitExecutor.serialize());
            System.out.println("E Salvando: " + circuitExecutor.serialize());
        }
        tag.putBoolean("executing", executing);
        Inventories.toTag(tag, inventoryItems);
        return super.toTag(tag);
    }

	/**
	 * Método que retorna dados a partir de uma dada tag
	 * @param tag		Tag que recuperará os dados
	 */
    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        Inventories.fromTag(tag, inventoryItems);
        executing = tag.getBoolean("executing");
        circuitDescStr = tag.getString("circuit_description");
        circuitStateStr = tag.getString("execution_state");
        loadedFromSave = true;
    }

    private void scheduleTick() {
        assert world != null;
        world.getBlockTickScheduler().schedule(pos, QuartusBlocks.EXECUTOR, EXECUTION_DELAY);
    }

    private boolean isFloppyValid() {
        if (inventoryItems == null || inventoryItems.size() == 0) return false;
        ItemStack floppyDisk = inventoryItems.get(0);
        if (!floppyDisk.getItem().equals(QuartusItems.FLOPPY_DISK)) return false;
        return floppyDisk.getOrCreateTag().contains("circuit") && !floppyDisk.getOrCreateTag().getString("circuit").isEmpty();
    }

    private void loadCircuitDescriptorFromInv() {
        if (isFloppyValid()) {
            ItemStack floppyDisk = inventoryItems.get(0);
            circuitDescriptor = new CircuitDescriptor.Serializer().unserialize(floppyDisk.getOrCreateTag().getString("circuit"));
        }
    }

    private void scanIOPorts() {
        if (world == null || circuitDescriptor == null) {
            System.out.println("Couldn't scan io ports");
            if (executing) stopExecution();
            return;
        }

        int minimumIOBlocks = Math.max(circuitDescriptor.getInputCount(), circuitDescriptor.getOutputCount());
        inputsPos = new ArrayList<>();
        outputsPos = new ArrayList<>();

        BlockState executorBs = world.getBlockState(pos);
        Direction chainOutDirection = executorBs.get(Properties.HORIZONTAL_FACING).rotateYCounterclockwise();

        BlockPos IOBlockPos = pos.offset(chainOutDirection);
        BlockState IOBlockState;
        for (int i = 0; i < minimumIOBlocks; i++) {
            IOBlockState = world.getBlockState(IOBlockPos);
            if (!(IOBlockState.getBlock() instanceof ExecutorIOBlock)) break;

            ExecutorIOBlock.ExecutorIOState executorIOState = IOBlockState.get(ExecutorIOBlock.EXTENSOR_STATE);

            if (executorIOState == ExecutorIOBlock.ExecutorIOState.IO) {
                inputsPos.add(IOBlockPos);
                outputsPos.add(IOBlockPos);
            }
            else if (executorIOState == ExecutorIOBlock.ExecutorIOState.OUPUT)
                outputsPos.add(IOBlockPos);
            else if (executorIOState == ExecutorIOBlock.ExecutorIOState.INPUT)
                inputsPos.add(IOBlockPos);

            IOBlockPos = IOBlockPos.offset(chainOutDirection);
        }

        //Se houver menos portas I/O que o necessário, pare a execução
        if (inputsPos.size() != circuitDescriptor.getInputCount() || outputsPos.size() != circuitDescriptor.getOutputCount()) {
            if (executing) stopExecution();
            errorMessage = "blockentity.quartus.executor.not_enough_io";
        }
    }

    private void createCircuitExecutor() {
        if (circuitDescriptor == null || world == null) {
            if (executing) stopExecution();
            return;
        }

        try {
            circuitExecutor = new CircuitExecutor.Builder().setWorld(world).setInputControllersPos(inputsPos).setOutputControllersPos(outputsPos).setCircuitDescriptor(circuitDescriptor).build();
        } catch (Exception e) {
            System.out.println("Error creating circuit executor");
            e.printStackTrace();
            if (executing) stopExecution();
        }
    }



    //Redefine o estado de todas as portas I/O conectadas ao executor de volta para void (e a última para void_end)
    private void resetIOStatesToVoid() {
        if (world == null) {
            return;
        }

        BlockState executorBs = world.getBlockState(pos);

        Direction executorFacingDir = executorBs.get(Properties.HORIZONTAL_FACING);
        Direction chainOutDirection = executorFacingDir.rotateYCounterclockwise();
        BlockPos IOBlockPos = pos.offset(chainOutDirection);

        while (world.getBlockState(IOBlockPos).getBlock() == QuartusBlocks.EXECUTOR_IO) {
            world.setBlockState(IOBlockPos, QuartusBlocks.EXECUTOR_IO.getDefaultState().with(Properties.HORIZONTAL_FACING, executorFacingDir), 2);
            IOBlockPos = IOBlockPos.offset(chainOutDirection);
        }

        IOBlockPos = IOBlockPos.offset(chainOutDirection.getOpposite());
        if (world.getBlockState(IOBlockPos).getBlock() == QuartusBlocks.EXECUTOR_IO)
            world.setBlockState(IOBlockPos, QuartusBlocks.EXECUTOR_IO.getDefaultState().with(ExecutorIOBlock.EXTENSOR_STATE, ExecutorIOBlock.ExecutorIOState.VOID_END).with(Properties.HORIZONTAL_FACING, executorFacingDir), 2);
    }

    /**
     *  Função que inicia o processo de execução do circuito.
     *  Passos:
     *      1) Lê o circuito do disquete inserido
     *      2) Percorre a corrente de portas I/O e salva a posição delas
     *      3) Substitui os inputs e outputs do circuito pelas portas I/O escaneadas
     *      4) Inicia um loop de atualização (na função tick())
     *
     *  OBS: se houver algum erro nos passos acima, a flag executing se tornará false antes do fim dessa função.
     *  @param startIssuer - player que solicitou a execução do circuito (às vezes nenhum player solicita)
     */
    public void startExecution(@Nullable PlayerEntity startIssuer) {
        executing = true;
        errorMessage = "blockentity.quartus.executor.unknown_error";
        //Passos 1, 2 e 3
        loadCircuitDescriptorFromInv();
        scanIOPorts();
        createCircuitExecutor();

        if (circuitExecutor == null) stopExecution();
        if (executing) { //Se nenhuma etapa falhou, agende o tick
            //Passo 4
            scheduleTick();
            return;
        }

        if (startIssuer != null) { //Se houver erros na execução, informe o jogador que a iniciou
            try {
                startIssuer.sendMessage(new TranslatableText(errorMessage));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else //Se nenhum jogador houver iniciado o processo de execução, exiba o erro no log
            Quartus.LOGGER.error("An error has occurred @ExecutorBlockEntity::startExecution(), but no issuer (player) has been found: " + errorMessage);
    }

    /* Parar a execução significa destruir o circuito interno (mantendo o disquete intacto),
        parar o loop de atualização e redefinir o estado das portas I/O
     */
    public void stopExecution() {
        executing = false;
        circuitDescriptor = null;
        circuitExecutor = null;
        resetIOStatesToVoid();
    }

    public void resumeExecution() {
        executing = true;
        if (circuitDescStr.length() == 0){
            stopExecution();
            return;
        }

        circuitDescriptor = new CircuitDescriptor.Serializer().unserialize(circuitDescStr);

        if (circuitStateStr.length() == 0) {
            stopExecution();
            return;
        }

        scanIOPorts();
        try {
            circuitExecutor = new CircuitExecutor.Serializer().unserialize(circuitStateStr)
                    .setCircuitDescriptor(circuitDescriptor)
                    .setInputControllersPos(inputsPos)
                    .setOutputControllersPos(outputsPos)
                    .setWorld(world)
                    .build();
        } catch (Exception e) {
            System.out.println("Failed to create circuit executor (resuming executor)");
            e.printStackTrace();
            return;
        }
        scheduleTick();
    }

    public void onScheduledTick() {
        /* Nesse momento, existem duas possibilidades de estado:
            1) O executor está em execução normal
            2) O mundo está sendo carregado e o executor tem que retomar as atividades
         */


        if (!isFloppyValid()) stopExecution();
        if (!executing) return;

        //Para o caso 2, temos que nos certificar que o circuito não é nulo
        if (circuitExecutor == null) {
            //Se o mundo tiver acabado de ser carregado (caso 2), o circuito é nulo, então solicite o recomeço da simulação
            resumeExecution();
            return;
        }

        for (int i = 0; i < 20; i++)
            circuitExecutor.updateCircuit();

        //Chama a própria função tick após um intervalo
        scheduleTick();
    }

    //Sempre que algum bloco é colocado ou alterado na chain de IO
    public void chainChanged() {
        if (executing)
            scanIOPorts();
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        fromTag(compoundTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return toTag(compoundTag);
    }

}
