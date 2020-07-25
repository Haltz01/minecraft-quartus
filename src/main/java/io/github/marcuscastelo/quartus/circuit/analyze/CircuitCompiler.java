package io.github.marcuscastelo.quartus.circuit.analyze;

import io.github.marcuscastelo.quartus.block.QuartusInGameComponent;
import io.github.marcuscastelo.quartus.circuit.CircuitUtils;
import io.github.marcuscastelo.quartus.circuit.QuartusCircuit;
import io.github.marcuscastelo.quartus.circuit.components.CircuitComponent;
import io.github.marcuscastelo.quartus.circuit.components.CircuitInput;
import io.github.marcuscastelo.quartus.circuit.components.CircuitOutput;
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
    QuartusCircuit circuit;
    World world;

    Queue<BlockPos> explorePoll;
    Map<BlockPos, CircuitComponent> componentInPos;

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
        this.circuit = new QuartusCircuit();
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
                    CircuitComponent node = ((QuartusInGameComponent) nodeBlock).createCircuitComponent();
                    componentInPos.putIfAbsent(nodePos, node);
                    circuit.addComponent(node);
                    if (node instanceof CircuitInput)
                        explorePoll.add(nodePos);
                }
            }
        }
    }

    //TODO: pensar em forma de ordernar os inputs e outputs (colorar cnomes?)
    //TODO: outline bonita com render e indicação de componentes incorretos (pós-entrega)


    //TODO: tratar quando o circuito sai pra fora do tamanho máximo (dá NullPointerException agora -> circuit.addLink(nextNodeInfo.AtoB, nextNodeInfo.BtoA, component, componentInPos.get(nextNodePos)) );
	/**
	 * Método auxiliar que explora o circuito mapeado pelo scanCircuitNodes
	 */
	private void exploreCircuit() {
        while (explorePoll.peek() != null) {
            BlockPos nodePos = explorePoll.poll();
            System.out.println("Explorando a pos " + nodePos.toString());

            CircuitComponent component = componentInPos.getOrDefault(nodePos, null);
            if (component == null) {
                errorMessage = new TranslatableText("circuitcompiler.out_of_bounds", nodePos.toString());
                failed = true;
                return;
            }

            //Se a posição já foi explorada
            if (component.hasOutputConnections()) continue;

            // TODO: mais ou menos por aqui deve ser adicionado um tratamento para os extensores e distribuidores!! -> eles não existem de verdade, são só facilitadores do que queremos fazer
            // TODO: extensor só pode ser conectado em GATES!!
            // Guerra vai fazer!!

            // Percorre os fios a partir de um node
            // Retorna 0 ou 1 nodes na maioria dos casos
            // Caso "especial": distribuidor -> a saída de um outro gate gera mais de um fio para vários inputs (de outros gates)
            // Caso "especial": extensores -> aumentam a quantidade de inputs de um gate
            //TODO: resolver parâmetros redundantes
            List<CircuitUtils.ConnectedNodeInfo> nextComponentsPos = CircuitUtils.getConnectedNodesInfo(world, circuit, component, nodePos);
            if (nextComponentsPos.size() == 0 && !(component instanceof CircuitOutput)) {
                errorMessage = new TranslatableText("circuitcompiler.disconnected_component");
                failed = true;
            }

            for (CircuitUtils.ConnectedNodeInfo nextNodeInfo : nextComponentsPos) {
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
    public Optional<QuartusCircuit> compile() {
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