package io.github.marcuscastelo.quartus.circuit.analyze;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.circuit.QuartusCircuit;
import jdk.internal.jline.internal.Nullable;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CircuitCompiler {
    BlockPos startPos, endPos;
    QuartusCircuit circuit;
    World world;

    Queue<BlockPos> explorePoll;

    public CircuitCompiler(World world, BlockPos startPos, BlockPos endPos) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.world = world;
        this.circuit = new QuartusCircuit();
        explorePoll = new LinkedList<>();
    }

    private void scanCircuitNodes() {
        int startX, startY, startZ;
        // TODO: Mudar valores iniciais e finais de X, Y e Z -> devem sempre começar da mesma posição (canto "superior" esquerdo do circuito - olhando para o circuito)
        startX = Math.min(startPos.getX(), endPos.getX());
        startY = Math.min(startPos.getY(), endPos.getY());
        startZ = Math.min(startPos.getZ(), endPos.getZ());

        int endX, endY, endZ;
        endX = Math.max(startPos.getX(), endPos.getX());
        endY = Math.max(startPos.getY(), endPos.getY());
        endZ = Math.max(startPos.getZ(), endPos.getZ());

        if (startY != endY) throw new UnsupportedOperationException("Só se trabalha com y's iguais por enquanto");

        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                for (int y = startY; y <= endY; y++) {

                }
            }
        }
    }

    private void exploreCircuit() {
        while (explorePoll.peek() != null) {
            BlockPos nodePos = explorePoll.poll();
        }
    }

    public QuartusCircuit compile() {
        scanCircuitNodes();
        exploreCircuit();

        return circuit;
    }
}