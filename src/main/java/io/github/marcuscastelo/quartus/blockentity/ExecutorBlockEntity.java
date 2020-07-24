package io.github.marcuscastelo.quartus.blockentity;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.block.ExecutorIOBlock;
import io.github.marcuscastelo.quartus.circuit.QuartusCircuit;
import io.github.marcuscastelo.quartus.circuit.components.CircuitInput;
import io.github.marcuscastelo.quartus.circuit.components.CircuitOutput;
import io.github.marcuscastelo.quartus.circuit.components.executor.WorldInput;
import io.github.marcuscastelo.quartus.circuit.components.executor.WorldOutput;
import io.github.marcuscastelo.quartus.registry.QuartusBlockEntities;
import io.github.marcuscastelo.quartus.registry.QuartusBlocks;
import io.github.marcuscastelo.quartus.registry.QuartusItems;
import jdk.internal.jline.internal.Nullable;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Classe que define o BlockEntity do Executor
 */
public class ExecutorBlockEntity extends BlockEntity implements ImplementedInventory {
	//Variável que define o tick/clock do executor
    public static final int EXECUTION_DELAY = 2; // cada tick equivale a 1/20 segundos

	//Variáveis auxiliares
    boolean executing = false;
    QuartusCircuit currentCircuit = null;
    DefaultedList<ItemStack> inventoryItems;
    String errorMessage = "blockentity.quartus.executor.unknown_error";

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
	 * Método que setta a localização do blockEntity a uma posição no mundo
	 * @param world
	 * @param pos
	 */
    @Override
    public void setLocation(World world, BlockPos pos) {
        super.setLocation(world, pos);
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
    }

    public boolean hasCircuit() {
        return currentCircuit != null;
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

    private void loadCircuitFromInv() {
        if (isFloppyValid()) {
            ItemStack floppyDisk = inventoryItems.get(0);
            currentCircuit = new QuartusCircuit();
            currentCircuit.unserialize(floppyDisk.getOrCreateTag().getString("circuit"));
        }
    }

    private void scanIOPorts() {
        assert world != null;
        if (currentCircuit == null) {
            if (executing)
                stopExecution();
            return;
        }

        int minimumIOBlocks = Math.max(currentCircuit.getInputCount(), currentCircuit.getOutputCount());
        inputsPos = new ArrayList<>();
        outputsPos = new ArrayList<>();

        BlockState executorBs = world.getBlockState(pos);
        Direction chainOutDirection = executorBs.get(Properties.HORIZONTAL_FACING).rotateYCounterclockwise();

        BlockPos IOBlockPos = pos.offset(chainOutDirection);
        BlockState IOBlockState;
        for (int i = 0; i < minimumIOBlocks; i++) {
            IOBlockState = world.getBlockState(IOBlockPos);
            if (IOBlockState.getBlock() != QuartusBlocks.EXTENSOR_IO) break;

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
        if (inputsPos.size() != currentCircuit.getInputCount() || outputsPos.size() != currentCircuit.getOutputCount()) {
            stopExecution();
            errorMessage = "blockentity.quartus.executor.not_enough_io";
        }
    }

    private void overwriteCircuitIO()  {
        if (currentCircuit == null) {
            if (executing)
                stopExecution();
            return;
        }

        if (inputsPos.size() != currentCircuit.getInputCount()) {
            Quartus.LOGGER.error("Invalid inputs state at ExecutorBlockEntity::overwriteCircuitIO()");
            return;
        }
        if (outputsPos.size() != currentCircuit.getOutputCount()) {
            Quartus.LOGGER.error("Invalid outputs state at ExecutorBlockEntity::overwriteCircuitIO()");
            return;
        }

        int inputPosInd = 0;
        for (CircuitInput input: currentCircuit.getInputs()) {
            int inputID = input.getID();
            WorldInput worldInput = new WorldInput(world, inputsPos.get(inputPosInd++), input);
            currentCircuit.setComponentAtID(inputID, worldInput);
        }

        int outputPosInd = 0;
        for (CircuitOutput output: currentCircuit.getOutputs()) {
            int outputID = output.getID();
            WorldOutput worldOutput = new WorldOutput(world, outputsPos.get(outputPosInd++), output);
            currentCircuit.setComponentAtID(outputID, worldOutput);
        }
    }

    //Redefine o estado de todas as portas I/O conectadas ao executor de volta para void (e a última para void_end)
    private void resetIOStatesToVoid() {
        assert world != null;
        BlockState executorBs = world.getBlockState(pos);

        Direction executorFacingDir = executorBs.get(Properties.HORIZONTAL_FACING);
        Direction chainOutDirection = executorFacingDir.rotateYCounterclockwise();
        BlockPos IOBlockPos = pos.offset(chainOutDirection);

        while (world.getBlockState(IOBlockPos).getBlock() == QuartusBlocks.EXTENSOR_IO) {
            world.setBlockState(IOBlockPos, QuartusBlocks.EXTENSOR_IO.getDefaultState().with(Properties.HORIZONTAL_FACING, executorFacingDir));
            IOBlockPos = IOBlockPos.offset(chainOutDirection);
        }

        IOBlockPos = IOBlockPos.offset(chainOutDirection.getOpposite());
        if (world.getBlockState(IOBlockPos).getBlock() == QuartusBlocks.EXTENSOR_IO)
            world.setBlockState(IOBlockPos, QuartusBlocks.EXTENSOR_IO.getDefaultState().with(ExecutorIOBlock.EXTENSOR_STATE, ExecutorIOBlock.ExecutorIOState.VOID_END).with(Properties.HORIZONTAL_FACING, executorFacingDir));
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
        loadCircuitFromInv();
        scanIOPorts();
        overwriteCircuitIO();

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

        stopExecution();
    }

    /* Parar a execução significa destruir o circuito interno (mantendo o disquete intacto),
        parar o loop de atualização e redefinir o estado das portas I/O
     */
    public void stopExecution() {
        executing = false;
        currentCircuit = null;
        resetIOStatesToVoid();
    }

    public void tick() {
        /* Nesse momento, existem duas possibilidades de estado:
            1) O executor está em execução normal
            2) O mundo está sendo carregado e o executor tem que retomar as atividades
         */

        if (!isFloppyValid()) stopExecution();
        if (!executing) return;

        //TODO: manter o estado do circuito através de reinicializações do servidor (atualmente ele inicia do zero)
        //Para o caso 2, temos que nos certificar que o circuito não é nulo
        if (!hasCircuit()) {
            //Se o mundo tiver acabado de ser carregado (caso 2), o circuito é nulo, então solicite o recomeço da simulação
            startExecution(null);
            return;
        }

        for (int i = 0; i < 20; i++)
            currentCircuit.updateCircuit();

        //Chama a própria função tick após um intervalo
        scheduleTick();
    }

    //Sempre que algum bloco é colocado ou alterado na chain de IO
    public void chainChanged() {
        scanIOPorts();
    }
}
