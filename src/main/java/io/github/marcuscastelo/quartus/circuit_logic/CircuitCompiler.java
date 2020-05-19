package io.github.marcuscastelo.quartus.circuit_logic;

import io.github.marcuscastelo.quartus.Quartus;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CircuitCompiler {
    BlockPos startPos, endPos;
    QuartusCircuit program;
    World world;

    Queue<BlockPos> explorePoll;

    public CircuitCompiler(World world, BlockPos compilerPos, BlockPos startPos, BlockPos endPos) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.world = world;
        this.program = new QuartusCircuit(world, compilerPos);
        explorePoll = new LinkedList<>();
    }

    private QuartusNode scanNodeAt(BlockPos pos) {
        Block nodeBlock = world.getBlockState(pos).getBlock();
        if (nodeBlock instanceof QuartusNodeConvertible)
            return ((QuartusNodeConvertible) nodeBlock).createQuartusNode(world, pos);
        else return null;
    }

    //Adiciona todos os inputs na area de escaneamento à lista de exploração
    private void scanProgramNodes() {
        int startX, startY, startZ;
        startX = Math.min(startPos.getX(), endPos.getX());
        startY = Math.min(startPos.getY(), endPos.getY());
        startZ = Math.min(startPos.getZ(), endPos.getZ());

        int endX, endY, endZ;
        endX = Math.max(startPos.getX(), endPos.getX());
        endY = Math.max(startPos.getY(), endPos.getY());
        endZ = Math.max(startPos.getZ(), endPos.getZ());

        if (startY != endY) throw new UnsupportedOperationException("Só se trabalha com y's iguais por enquanto");

        Quartus.LOGGER.debug("Iniciando Scan da região");
        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                for (int y = startY; y <= endY; y++) {
                    QuartusNode node = scanNodeAt(new BlockPos(x,y,z));
                    if (node == null) {
//                        System.out.printf("%d, %d, %d Não é um node\n", x, y, z );
                        continue;
                    }
                    BlockPos nodePos = new BlockPos(x,y,z);
                    System.out.println(String.format("Encontrei um node em %d, %d, %d", x,y,z));
                    program.nodeInPosition.putIfAbsent(nodePos, node);
                    if (node instanceof QuartusInput) {
                        System.out.println(String.format("É um input!!", x,y,z));
                        explorePoll.add(nodePos);
                    }
                }
            }
        }
    }


    //Fios já foram tratados, aqui só haverá Nodes
    private void exploreCircuit() {
        while (explorePoll.peek() != null) {
            BlockPos nodePos = explorePoll.poll();
            if (program.isNodeAlreadyExplored(program.getNodeAt(nodePos))) continue;

            System.out.println("Explorando a pos " + nodePos.toString());
            QuartusNode node = program.getNodeAt(nodePos);
            assert node != null;

            List<QuartusNode> nextNodes = CircuitUtils.getConnectedNodes(program, node);
            for (QuartusNode nextNode: nextNodes) {
                BlockPos nextPos = nextNode.pos;
                System.out.println("Vizinho: " + nextPos.toString());

                program.addLink(node, nextNode);

                if (program.isNodeAlreadyExplored(nextNode)) continue;
                explorePoll.offer(nextPos);
            }
        }
    }

    //Escaneia uma área e retorna um grafo orientado representando as conexões entre os nós do circuito
    public QuartusCircuit compile() {
        System.out.println("[Compile] * Iniciando Compilação... *");

        System.out.println("[Compile] Escaneando nodes...");
        scanProgramNodes();
        System.out.println("[Compile] Escaneamento completo!");

        System.out.println("[Compile] Explorando nodes...");
        exploreCircuit();
        System.out.println("[Compile] Exploração completa!");

        System.out.println("[Compile] * Compilação completa! *");

        return program;
    }
}
