package io.github.marcuscastelo.quartus.circuit_logic;

import io.github.marcuscastelo.quartus.registry.QuartusBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.math.Direction;

public class CircuitExecutor {
    private final World world;
    private final BlockPos executorPosition;

    public boolean isPaused() {
        return paused;
    }

    private boolean paused;

    public void setCircuit(QuartusCircuitExplorer circuit) {
        this.circuit = circuit;
        inputsPower = new boolean[circuit.getInputCount()];
        outputsPower = new boolean[2];//circuit.getOutputCount()];

        extensorsIOPosition = new BlockPos[2];//Math.max(circuit.getInputCount(), circuit.getOutputCount())];
        initExtensorsIOPosition();
    }

    public void start() {
        paused = false;
        tick();
    }

    public void stop() {
        paused = true;
    }

    private QuartusCircuitExplorer circuit;

    private boolean[] inputsPower = {}, outputsPower = {};
    private BlockPos[] extensorsIOPosition = {};

    private void initExtensorsIOPosition() {
        Direction exploreDirection = world.getBlockState(executorPosition).get(Properties.HORIZONTAL_FACING).rotateYCounterclockwise();
        BlockPos extensorPos = executorPosition.offset(exploreDirection);
        for (int i = 0; i < extensorsIOPosition.length; i++) {
            extensorsIOPosition[i] = extensorPos;
            extensorPos = extensorPos.offset(exploreDirection);
        }
    }

    public CircuitExecutor(World world, BlockPos executorPosition, QuartusCircuitExplorer circuit) {
        this.world = world;
        this.executorPosition = executorPosition;
        this.paused = true;

        if (circuit != null)
            setCircuit(circuit);
    }

    private void updateInputsFromGame() {
        if (circuit.getInputCount() == 0) return;
        BlockState extensorBs;
        BlockPos extensorPos;
        Direction facingDir = world.getBlockState(extensorsIOPosition[0]).get(Properties.HORIZONTAL_FACING);
        for (int i = 0; i < circuit.getInputCount(); i++) {
            extensorPos = extensorsIOPosition[i];
            extensorBs = world.getBlockState(extensorPos);

            int power = extensorBs.getWeakRedstonePower(world, extensorPos, facingDir);
            System.out.println(i +  " = " + power);
        }
    }

    private void propagateChanges() {

    }

    boolean test = false;
    private void updateOutputsToGame() {
        System.out.println("Ou - " + circuit.getOutputCount());
        test = !test;
        BlockPos extensorPos;
        for (int i = 0; i < 2; i++) {
            extensorPos = extensorsIOPosition[i];
            BlockState bs = world.getBlockState(extensorPos);
            world.setBlockState(extensorPos, bs.with(Properties.POWERED, test));
        }
    }

    public void tick() {
        if (circuit == null) paused = true;

        if (paused) return;
        updateInputsFromGame();
        propagateChanges();
        updateOutputsToGame();

        if (world == null) {
            System.err.println("ERROR: null world");
            return;
        }

        world.getBlockTickScheduler().schedule(executorPosition, QuartusBlocks.EXECUTOR, 20);
    }
}
