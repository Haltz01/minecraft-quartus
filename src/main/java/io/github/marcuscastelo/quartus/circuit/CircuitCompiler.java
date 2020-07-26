package io.github.marcuscastelo.quartus.circuit;

import io.github.marcuscastelo.quartus.block.QuartusInGameComponent;
import io.github.marcuscastelo.quartus.circuit.CircuitDescriptor;
import io.github.marcuscastelo.quartus.circuit.CircuitExplorer;
import io.github.marcuscastelo.quartus.circuit.components.ComponentDescriptor;
import io.github.marcuscastelo.quartus.circuit.components.InputDescriptor;
import io.github.marcuscastelo.quartus.circuit.components.OutputDescriptor;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;


//TODO: renomear variávies para nova terminologia
//TODO: manter ordem dos inputs e outputs

/**
 * Classe que define o CircuitCompiler - Compilador do circuito
 */
public class CircuitCompiler {
	//Variáveis que auxiliam no mapeamento do circuito
    BlockPos startPos, endPos;
    CircuitDescriptor circuit;
    World world;

    Queue<BlockPos> explorePoll;
    Map<BlockPos, ComponentDescriptor> componentInPos;

    Text errorMessage;
    boolean failed;

	/**
	 * Construtor padrão da classe CircuitCompiler
	 * @param world		Mundo em que está sendo jogado
	 * @param startPos		Posição inicial para fazer o escaneamento
	 * @param endPos		Posição final para fazer o escaneamento
	 */
    public CircuitCompiler(World world, BlockPos startPos, BlockPos endPos) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.world = world;
        this.circuit = new CircuitDescriptor();
        this.explorePoll = new LinkedList<>();
        this.componentInPos = new HashMap<>();
        this.errorMessage = new TranslatableText("circuitcompiler.unknown_error", 1);
        failed = false;
    }

	/**
	 * Método auxiliar que escaneia o circuito, mapeando os componentes encontrados
	 * e adicionando-os em uma fila
	 */
    private void scanCircuitNodes() {
        System.out.println("[Compile] Começando compilação");
		//Define o começo do escaneamento
        int startX, startY, startZ;
        // TODO: Mudar valores iniciais e finais de X, Y e Z -> devem sempre começar da mesma posição (canto "superior" esquerdo do circuito - olhando para o circuito)
        startX = Math.min(startPos.getX(), endPos.getX());
        startY = Math.min(startPos.getY(), endPos.getY());
        startZ = Math.min(startPos.getZ(), endPos.getZ());
		
		//Define as coordenadas finais para o escaneamento
        int endX, endY, endZ;
        endX = Math.max(startPos.getX(), endPos.getX());
        endY = Math.max(startPos.getY(), endPos.getY());
        endZ = Math.max(startPos.getZ(), endPos.getZ());

		//Faz o escaneamento de acordo com o início e o fim calculados
        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                for (int y = startY; y <= endY; y++) {
                    BlockPos nodePos = new BlockPos(x,y,z);
                    Block nodeBlock = world.getBlockState(nodePos).getBlock();
                    if (!(nodeBlock instanceof QuartusInGameComponent)) continue;
                    System.out.println("[Compile] Encontrado um " + nodeBlock.getName().asString() + " em " + nodePos);
                    ComponentDescriptor node = ((QuartusInGameComponent) nodeBlock).createCircuitComponent(circuit);
                    componentInPos.putIfAbsent(nodePos, node);
                    circuit.addComponent(node);
                    if (node instanceof InputDescriptor)
                        explorePoll.add(nodePos);
                }
            }
        }
    }

    //TODO: indicação de componentes incorretos (pós-entrega)

	/**
	 * Método auxiliar que explora o circuito mapeado pelo scanCircuitNodes
	 */
	private void exploreCircuit() {
        while (explorePoll.peek() != null) {
            BlockPos nodePos = explorePoll.poll();
            System.out.println("Explorando a pos " + nodePos.toString());

            ComponentDescriptor component = componentInPos.getOrDefault(nodePos, null);
            if (component == null) {
                errorMessage = new TranslatableText("circuitcompiler.out_of_bounds", nodePos.toString());
                failed = true;
                return;
            }

            //Se a posição já foi explorada
            if (component.hasOutputConnections()) continue;

            // Percorre os fios a partir de um node
            // Retorna 0 ou 1 nodes na maioria dos casos
            // Caso "especial": distribuidor -> a saída de um outro gate gera mais de um fio para vários inputs (de outros gates)
            // Caso "especial": extensores -> aumentam a quantidade de inputs de um gate
            //TODO: resolver parâmetros redundantes
            List<CircuitExplorer.ConnectedBlocksInfo> nextComponentsPos = CircuitExplorer.getConnectedNodesInfo(world, circuit, component, nodePos);
            if (nextComponentsPos.size() == 0 && !(component instanceof OutputDescriptor)) {
                errorMessage = new TranslatableText("circuitcompiler.disconnected_component");
                failed = true;
            }

            for (CircuitExplorer.ConnectedBlocksInfo nextNodeInfo : nextComponentsPos) {
                BlockPos nextNodePos = nextNodeInfo.bPos;

                System.out.println("Vizinho: " + nextNodePos);

                System.out.println(nextNodeInfo.AtoB + " --- " + nextNodeInfo.BtoA + " ::: " + component + " --- " + componentInPos.get(nextNodePos));
                if (componentInPos.getOrDefault(nextNodePos,null) == null) {
                    errorMessage = new TranslatableText("circuitcompiler.out_of_bounds", nextNodePos.toString());
                    failed = true;
                    return;
                }

                circuit.addLink(nextNodeInfo.AtoB, nextNodeInfo.BtoA, component, componentInPos.get(nextNodePos));

                if (componentInPos.get(nextNodePos).hasOutputConnections()) {
                    System.out.println("Vizinho já explorado... ignorando!");
                } else {
                    System.out.println("Adiconando Vizinho: " + nextNodePos.toString());
                    explorePoll.offer(nextNodePos);
                }
            }
        }
        System.out.println("[Explore] Exploracão completa!");
    }

	/**
	 * Método que faz a compilação do circuito, escaneando-o, mapeando seus componentes
	 * e retornando o circuito já estudado
	 * @return		Circuito compilado
	 */
    public Optional<CircuitDescriptor> compile() {
        this.errorMessage = new TranslatableText("circuitcompiler.unknown_error", 2);
        failed = false;
        scanCircuitNodes();
        exploreCircuit();

        if (failed && MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.sendMessage(errorMessage);
            return Optional.empty();
        }

        return Optional.of(circuit);
    }
}