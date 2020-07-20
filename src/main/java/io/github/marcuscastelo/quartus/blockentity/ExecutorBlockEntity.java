package io.github.marcuscastelo.quartus.blockentity;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.block.ExecutorIOBlock;
import io.github.marcuscastelo.quartus.circuit.QuartusCircuit;
import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitInput;
import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitOutput;
import io.github.marcuscastelo.quartus.circuit.components.executor.WorldInput;
import io.github.marcuscastelo.quartus.circuit.components.executor.WorldOutput;
import io.github.marcuscastelo.quartus.registry.QuartusBlockEntities;
import io.github.marcuscastelo.quartus.registry.QuartusBlocks;
import io.github.marcuscastelo.quartus.registry.QuartusItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

//TODO: traduzir mensagens de erro no lang
public class ExecutorBlockEntity extends BlockEntity implements ImplementedInventory {
    boolean executing = false;
    QuartusCircuit currentCircuit = null;
    DefaultedList<ItemStack> inventoryItems;
    String errorMessage = "blockentity.quartus.executor.unknown_error";

    List<BlockPos> inputsPos = new ArrayList<>();
    List<BlockPos> outputsPos = new ArrayList<>();

    public ExecutorBlockEntity() {
        super(QuartusBlockEntities.EXECUTOR_BLOCK_ENTITY_TYPE);
        this.inventoryItems = DefaultedList.ofSize(1, ItemStack.EMPTY);
    }

    @Override
    public void setLocation(World world, BlockPos pos) {
        super.setLocation(world, pos);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventoryItems;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putBoolean("executing", executing);
        Inventories.toTag(tag, inventoryItems);
        return super.toTag(tag);
    }

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
        world.getBlockTickScheduler().schedule(pos, QuartusBlocks.EXECUTOR, 20);
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
        for (QuartusCircuitInput input: currentCircuit.getInputs()) {
            int inputID = input.getID();
            WorldInput worldInput = new WorldInput(world, inputsPos.get(inputPosInd++), inputID);
            currentCircuit.setComponentAtID(inputID, worldInput);
        }

        int outputPosInd = 0;
        for (QuartusCircuitOutput output: currentCircuit.getOutputs()) {
            int outputID = output.getID();
            WorldOutput worldOutput = new WorldOutput(world, outputsPos.get(outputPosInd++), outputID);
            currentCircuit.setComponentAtID(outputID, worldOutput);
        }
    }

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

    public void startExecution(PlayerEntity startIssuer) {
        executing = true;
        errorMessage = "blockentity.quartus.executor.unknown_error";
        loadCircuitFromInv();
        scanIOPorts();
        overwriteCircuitIO();

        if (executing) { //Se nenhuma etapa falhou, agende o tick
            scheduleTick();
        }
        else if (startIssuer != null) //Se houver erros na execução, informe o jogador que a iniciou
            startIssuer.sendMessage(new TranslatableText(errorMessage));
        else
            stopExecution();
    }

    public void stopExecution() {
        executing = false;
        currentCircuit = null;
        resetIOStatesToVoid();
    }

    //TODO: verificar integridade das portas IO (parar se forem quebradas)
    public void tick() {
        if (!isFloppyValid()) stopExecution();
        if (!executing) return;

        //TODO: definir inputs e outputs do circuito de acordo com os extensores IO
        //TODO: mensagem de erro extensores insuficientes
        currentCircuit.updateCircuit();

        scheduleTick();
    }

    //Sempre que algum bloco é colocado ou alterado na chain de IO
    public void chainChanged() {
        scanIOPorts();
    }
}
