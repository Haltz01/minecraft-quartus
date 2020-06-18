package io.github.marcuscastelo.quartus.circuit_logic;

import io.github.marcuscastelo.quartus.Quartus;
import jdk.internal.jline.internal.Nullable;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 * Classe responsável por analisar uma região no mundo e criar um objeto contendo
 * as informações do circuito construído. Tal objeto é do tipo {@link QuartusCircuit}.
 *
 * Iniciamente, é feita uma análise cúbica de todos os blocos da região, criando nós {@link QuartusNode} -
 * que representam blocos no jogo e suas respectivas funções lógicas no circuito - e os adicionando à
 * lista de exploração.
 *
 * Mais tarde, para cada nó da lista de exploração, faz-se a exploração do nó. Explorar um nó significa
 * obter os nós com os quais ele se conecta por meio de suas portas de output e adicioná-los ao grafo de
 * conexões (armazenado dentro de cada nó) e adicioná-los à lista de nós a serem explorados em seguida.
 *
 * Ao fim desse processo, temos um objeto que contêm todas as informações necessárias para a apresentação
 * em tempo real no jogo da simulação do circuito. Também é possível salvar o circuito em um disquete e integrá-lo
 * a outros circuitos mais complexos.
 *
 */
public class CircuitCompiler {
    private static boolean DEBUG_MESSAGES_ENABLED = true;

    BlockPos startPos, endPos;
    QuartusCircuit circuit;
    World world;

    Queue<BlockPos> explorePoll;

    public CircuitCompiler(World world, BlockPos compilerPos, BlockPos startPos, BlockPos endPos) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.world = world;
        this.circuit = new QuartusCircuit(world, compilerPos);
        explorePoll = new LinkedList<>();
    }

    @Nullable
    private QuartusNode createNodeAt(BlockPos pos) {
        Block nodeBlock = world.getBlockState(pos).getBlock();
        try {
            if (nodeBlock instanceof QuartusNodeConvertible)
                return ((QuartusNodeConvertible) nodeBlock).createQuartusNode(world, pos);
        } catch (QuartusNode.QuartusWrongNodeBlockException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    //Escaneia todos os nodes do circuito e os coloca num hashmap relacionando a posição com o node.
    //Adicionalmente, também alimenta uma fila de exploração com a posição de todos os inputs
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

        Quartus.LOGGER.debug("Iniciando Scan da região");
        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                for (int y = startY; y <= endY; y++) {
                    QuartusNode node = createNodeAt(new BlockPos(x,y,z));
                    if (node == null) continue; //Ignora blocos que não fazem parte do circuito

                    BlockPos nodePos = new BlockPos(x,y,z);
                    System.out.println(String.format("Encontrei um node em %d, %d, %d", x,y,z));
                    circuit.nodeInPosition.putIfAbsent(nodePos, node);
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
        System.out.println("[Explore] Iniciando exploração...");
        while (explorePoll.peek() != null) {

            BlockPos nodePos = explorePoll.poll();
            System.out.println("Explorando a pos " + nodePos.toString());

            if (circuit.isNodeAlreadyExplored(circuit.getNodeAt(nodePos))) {
                System.out.println("Posição já explorada... ignorando!");
                continue;
            }

            QuartusNode node = circuit.getNodeAt(nodePos);
            assert node != null;

            // TODO: mais ou menos por aqui deve ser adicionado um tratamento para os extensores e distribuidores!! -> eles não existem de verdade, são só facilitadores do que queremos fazer
            // TODO: extensor só pode ser conectado em GATES!!
            // Guerra vai fazer!!

            // Percorre os fios a partir de um node
            // Retorna 0 ou 1 nodes na maioria dos casos
            // Caso "especial": distribuidor -> a saída de um outro gate gera mais de um fio para vários inputs (de outros gates)
            // Caso "especial": extensores -> aumentam a quantidade de inputs de um gate
            List<QuartusNode> nextNodes = CircuitUtils.getConnectedNodes(circuit, node);

            for (QuartusNode nextNode: nextNodes) {
                BlockPos nextPos = nextNode.pos;
                System.out.println("Vizinho: " + nextPos.toString());

                circuit.addLink(node, nextNode);

                if (circuit.isNodeAlreadyExplored(nextNode)) {
                    System.out.println("Vizinho já explorado... ignorando!");
                } else {
                    System.out.println("Adiconando Vizinho: " + nextPos.toString());

                    explorePoll.offer(nextPos);
                }
            }
        }
        System.out.println("[Explore] Exploracão completa!");
    }

    //Escaneia uma área e retorna um grafo orientado representando as conexões entre os nós do circuito
    public QuartusCircuit compile() {
        System.out.println("[Compile] * Iniciando Compilação... *");

        System.out.println("[Compile] Escaneando nodes...");
        scanCircuitNodes();
        System.out.println("[Compile] Escaneamento completo!");

        System.out.println("[Compile] Explorando nodes...");
        exploreCircuit();
        System.out.println("[Compile] Exploração completa!");

        System.out.println("[Compile] * Compilação completa! *");

        return circuit;
    }
}
