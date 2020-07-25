package io.github.marcuscastelo.quartus.util;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.block.QuartusInGameComponent;
import io.github.marcuscastelo.quartus.block.circuit_parts.WireBlock;
import io.github.marcuscastelo.quartus.registry.QuartusBlocks;
import io.github.marcuscastelo.quartus.registry.QuartusProperties;
import jdk.internal.jline.internal.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.github.marcuscastelo.quartus.registry.QuartusProperties.UpValue;

/**
 * Classe que analisa as conexões dos fios e tenta conectá-los
 * da forma que possivelmente seriam organizados no mundo real
 */
public class WireConnector {
	//Variável enum que marca qual tipo de conexão o fio faz
    public enum UpValuesDirections { NONE, FIRST, SECOND, BOTH }

    /**
	 * Método auxiliar que verifica se houve mudanças no fio.
	 * Compara os BlockState's de antes e depois
	 * @param oldState		BlockState do fio antes de uma alteração
	 * @param newState		BlockState do fio após qualquer alteração
	 * @return		Boolean que diz se houve mudança
	 */
    private static boolean hasWireChanged(BlockState oldState, BlockState newState) {
        return oldState.get(Properties.HORIZONTAL_FACING) != newState.get(Properties.HORIZONTAL_FACING) ||
                oldState.get(QuartusProperties.WIRE_TURN) != newState.get(QuartusProperties.WIRE_TURN) ||
                oldState.get(QuartusProperties.WIRE_POSITIVE) != newState.get(QuartusProperties.WIRE_POSITIVE) ||
                oldState.get(QuartusProperties.WIRE_UP) != newState.get(QuartusProperties.WIRE_UP);
    }

	/**
	 * Método que verifica se a conexão do fio
	 * o faz 'virar'/é do tipo 'turn'
	 * @param facingDirection		Direção que o fio está 'olhando'
	 * @param auxDirection		Outro lado do facingDirection
	 * @return		Boolean que verifica se a conexão faz 'turn'
	 */
    public static boolean isConnectionTurned(Direction facingDirection, @Nullable Direction auxDirection) {
        return auxDirection != null && !facingDirection.getOpposite().equals(auxDirection);
    }

	/**
	 * Método que verifica se a conexão do fio é positiva
	 * Orientação positiva é quando a auxDirection equivale
	 * à rotação do facingDirection no sentido anti-horário,
	 * em 90° (derivado do conceito de bases positivas no espaço)
	 * @param facingDirection		Direção que o fio está 'olhando'
	 * @param auxDirection		Outro lado do facingDirection
	 * @return		Boolean que verifica se são equivalentes
	 */
    public static boolean isConnectionPositive(Direction facingDirection, @Nullable Direction auxDirection) {
        if (auxDirection == null) return false;
        return facingDirection.rotateYCounterclockwise().equals(auxDirection);
    }


	/**
	 * Método que retorna um par de direções horizontais, sem se preocupar com
	 * facing ou auxDirection. Se a quantidade de blocos foi <= 2,
	 * salva no par os blocos conectados de acordo com a direção,
	 * pois não é possível conectar um fio a mais de 2 componentes.
	 * @param mainWirePos		Posição do bloco principal
	 * @param otherBlocksPos		Lista de posições de bloco conectados ao bloco principal
	 * @return		Par com duas direções, que podem ser null
	 */
    public static Pair<Direction, Direction> getConnectionHorizontalDirections(BlockPos mainWirePos, List<BlockPos> otherBlocksPos) {
        if (otherBlocksPos.size() > 2) throw new UnsupportedOperationException("Não é possível conectar um fio a mais de 2");

        Direction direction1 = null, direction2 = null;
        if (otherBlocksPos.size() > 0)
            direction1 = DirectionUtils.getHorizontalDirectionAtoB(mainWirePos, otherBlocksPos.get(0));
        if (otherBlocksPos.size() > 1)
            direction2 = DirectionUtils.getHorizontalDirectionAtoB(mainWirePos, otherBlocksPos.get(1));

        return new Pair<>(direction1, direction2);

    }

	/**
	 * Método que retorna um par de direções verticais, sem se preocupar com
	 * facing ou auxDirection. Se a quantidade de blocos foi <= 2,
	 * salva no par os blocos conectados de acordo com a direção,
	 * pois não é possível conectar um fio a mais de 2 componentes.
	 * @param mainWirePos		Posição do bloco principal
	 * @param otherBlocksPos		Lista de posições de bloco conectados ao bloco principal
	 * @return		Par com duas direções, que podem ser null
	 */
    public static Pair<Direction, Direction> getConnectionVerticalDirections(BlockPos mainWirePos, List<BlockPos> otherBlocksPos) {
        if (otherBlocksPos.size() > 2) throw new UnsupportedOperationException("Não é possível conectar um fio a mais de 2");

        Direction direction1 = null, direction2 = null;
        if (otherBlocksPos.size() > 0)
            direction1 = DirectionUtils.getVerticalDirectionAtoB(mainWirePos, otherBlocksPos.get(0));
        if (otherBlocksPos.size() > 1)
            direction2 = DirectionUtils.getVerticalDirectionAtoB(mainWirePos, otherBlocksPos.get(1));

        return new Pair<>(direction1, direction2);
    }

	/**
	 * Método que retorna um EnumSet com bases no par de direções fornecidas
	 * De acordo com a orientação do fio, ele ajusta as direções conforme
	 * as ligações de facingDirection e auxDirection do fio
	 * @param verticalDirections		Par de direções verticais
	 * @return		EnumSet com as novas direções verticais
	 */
    public static EnumSet<UpValuesDirections> getConnectionUpValuesInfo(Pair<Direction, Direction> verticalDirections) {
        EnumSet<UpValuesDirections> upValuesDirections = EnumSet.of(UpValuesDirections.NONE);
        if (verticalDirections.getLeft() == Direction.UP) upValuesDirections.add(UpValuesDirections.FIRST);
        if (verticalDirections.getRight() == Direction.UP) {
            upValuesDirections.add(UpValuesDirections.SECOND);
            if (upValuesDirections.contains(UpValuesDirections.FIRST))
                upValuesDirections.add(UpValuesDirections.BOTH);
        }

        return upValuesDirections;
    }

	/**
	 * Método que faz a atualização do fio informado e os conecta com os componentes vizinhos,
	 * tentando encontrar a melhor combinação
	 * @param world		Mundo que está sendo jogado
	 * @param mainWirePos		Posição do fio principal
	 * @param otherWiresPos		Lista com posições dos outros fios
	 */
    public static void connectTo(World world, BlockPos mainWirePos, List<BlockPos> otherWiresPos) {
        if (otherWiresPos.size() > 2) throw new UnsupportedOperationException("Não é possível conectar um fio a mais de 2");
        /*
         *   --- Passos ---
         *   1) Se vazio, mantém facing e desconecta de todos
         *   2) Obtém informações sobre as direções do fio
         *   3) Obtém informações verticais do fio
         *   4) Define propriedades do fio com base em 2 e 3
         *   5) Atualiza o blockstate no mundo
         *   6) Cria um neighborUpdate artificial em parentes de níveis diferentes do nível atual
         */


        //Passo 1:
        if (otherWiresPos.size() == 0) {
            world.setBlockState(mainWirePos, QuartusBlocks.WIRE.getDefaultState().with(Properties.HORIZONTAL_FACING, world.getBlockState(mainWirePos).get(Properties.HORIZONTAL_FACING)), 2);
            return;
        }

        //Obtém o estado antigo e copia ele para o novo estado (que ainda será modificado)
        BlockState oldState = world.getBlockState(mainWirePos);
        BlockState newState;

        //Passo 2:
        Pair<Direction, Direction> horizontalDirections = getConnectionHorizontalDirections(mainWirePos, otherWiresPos);

        //Passo 3:
        Pair<Direction, Direction> verticalDirections = getConnectionVerticalDirections(mainWirePos, otherWiresPos);

        //Passo 4:
        UpValue newStateUpValue;
        Direction newFacingDirection = horizontalDirections.getLeft();
        Direction newAuxDirection = horizontalDirections.getRight();
        boolean newTurn = isConnectionTurned(newFacingDirection, newAuxDirection);
        boolean newPositive = isConnectionPositive(newFacingDirection, newAuxDirection);

        EnumSet<UpValuesDirections> upValuesDirections = getConnectionUpValuesInfo(verticalDirections);
        if (upValuesDirections.contains(UpValuesDirections.BOTH)) {
            newStateUpValue = UpValue.BOTH;
        } else if (upValuesDirections.contains(UpValuesDirections.SECOND)) {
            newStateUpValue = UpValue.FACING;
            System.out.println("ELE MESMO !!");
            Direction tempDirForSwap = newFacingDirection;
            //Troca a direção principal com a direção auxiliar (para manter o turn certo, muda a posividade)
            if (newTurn) newPositive = !newPositive;
            newFacingDirection = newAuxDirection;
            newAuxDirection = tempDirForSwap;

        } else if (upValuesDirections.contains(UpValuesDirections.FIRST)) {
            newStateUpValue = UpValue.FACING;
        } else {
            newStateUpValue = UpValue.NONE;
        }

        newState = oldState.with(Properties.HORIZONTAL_FACING, newFacingDirection)
                .with(QuartusProperties.WIRE_UP, newStateUpValue)
                .with(QuartusProperties.WIRE_POSITIVE, newPositive)
                .with(QuartusProperties.WIRE_TURN, newTurn);

        //Passo 5:
        world.setBlockState(mainWirePos, newState, 2);

        //Passo 6:
        if (hasWireChanged(oldState, newState)) {
            updateUnnaturalNeighborsIfWires(world, mainWirePos);
        }
    }

	/**
	 * Método que retorna a direção contrária da FACING do fio
	 * Como o fio pode girar, nem sempre a auxDirection é
	 * necessariamente a direção virada 180°
	 * @param wireBlockState		BlockState do fio, que contém as direções
	 * @return 	Direção do outro lado do fio
	 */
    public static Direction getAuxDirection(BlockState wireBlockState) {
        if (!(wireBlockState.getBlock() instanceof WireBlock)) throw new IllegalArgumentException("Blockstate must be a wire blockstate");
        return getAuxDirection(wireBlockState.get(Properties.HORIZONTAL_FACING), wireBlockState.get(QuartusProperties.WIRE_TURN), wireBlockState.get(QuartusProperties.WIRE_POSITIVE));
    }

	/**
	 * Método que retorna a direção contrária da FACING de um fio
	 * com base nos booleans que indica para qual sentido está virado
	 * Positivo	->	anti-horário
	 * Negativo	->	horário
	 * seguindo a regra da mão direita para uma base positivamente orientada
	 * @param facingDirection		Direção para o qual o fio 'olha'
	 * @param turned		Boolean que indica se está virado
	 * @param positive		Boolean que indica para qual sentido está virado
	 * @return		Direção contrária à facing
	 */
    public static Direction getAuxDirection(Direction facingDirection, boolean turned, boolean positive) {
        if (!turned) return facingDirection.getOpposite();

        if (positive) return facingDirection.rotateYCounterclockwise();
        else return facingDirection.rotateYClockwise();
    }

	/**
	 * Método que verifica quais fios possuem conexões não checadas
	 * @param world		Mundo que está sendo jogado
	 * @param mainWirePos		Posição do fio principal
	 * @return		Lista com posições de conexões não checadas
	 */
    public static List<BlockPos> getWireUncheckedConnections(World world, BlockPos mainWirePos) {
        BlockState wireBlockState = world.getBlockState(mainWirePos);
        if (wireBlockState.getBlock() != QuartusBlocks.WIRE) throw new IllegalArgumentException("Trying to get connections of a non-wire block");

        Direction facingDir = wireBlockState.get(Properties.HORIZONTAL_FACING);
        Direction auxDir = getAuxDirection(wireBlockState);

        //Navega o fio nas direções que ele aponta
        //Conexão principal é aquela que se encontra na direção "facing"
        BlockPos mainConnectionPos = navigateWire(world, mainWirePos, facingDir);
        BlockPos auxConnectionPos = navigateWire(world, mainWirePos, auxDir);

        List<BlockPos> uncheckedConnections = new ArrayList<>();

        if (mainConnectionPos != null) uncheckedConnections.add(mainConnectionPos);
        if (auxConnectionPos != null) uncheckedConnections.add(auxConnectionPos);

        return uncheckedConnections;
    }

	/**
	 * Método que verifica se um bloco na posição fornecida é um fio
	 * @param world		Mundo que está sendo jogado
	 * @param pos		Posição do bloco no mundo
	 * @return		Boolean que diz se o bloco em questão é um fio ou não
	 */
    private static boolean isWire(World world, BlockPos pos) { return world.getBlockState(pos).getBlock() == QuartusBlocks.WIRE; }

	/**
	 * Método que retorna uma lista com posições de blocos com conexões já estabelecidas
	 * @param world		Mundo que está sendo jogado
	 * @param mainWirePos		Posição do bloco no mundo
	 * @return		Lista com posições de blocos com conexões já estabelecidas
	 */
    public static List<BlockPos> getWireEstabilishedConnections(World world, BlockPos mainWirePos) {
        return getWireUncheckedConnections(world, mainWirePos).stream().filter(connectionPos -> !isWire(world,connectionPos) || areWiresConnected(world, mainWirePos, connectionPos)).collect(Collectors.toList());
    }


	//Verifica se ambos os fios apontam um para o outro
	/**
	 * Método que verifica se os fios nas posições dadas
	 * estão apontando um para o outro
	 * @param world		Mundo que está sendo jogado
	 * @param wireAPos		Posição do fio A
	 * @param wireBPos		Posição do fio B
	 * @return		Boolean que indica se os fios estão virados um para o outro
	 */
    public static boolean areWiresConnected(World world, BlockPos wireAPos, BlockPos wireBPos) {
        //TODO: remover exception de debug (retornar true)
        if (wireAPos == wireBPos) throw new IllegalArgumentException("Trying to check whether a wire is connected to itself");
        Direction AtoBHorDirection = DirectionUtils.getHorizontalDirectionAtoB(wireAPos, wireBPos);
        //TODO: remover exception de debug (retornar false)
        if (AtoBHorDirection == null) throw new IllegalArgumentException("Trying to check whether wires are horizontally-diagonally connected");

        Direction BtoAHorDirection = AtoBHorDirection.getOpposite();

        BlockState aWireBs = world.getBlockState(wireAPos);
        BlockState bWireBs = world.getBlockState(wireBPos);

        if (aWireBs.getBlock() != QuartusBlocks.WIRE || bWireBs.getBlock() != QuartusBlocks.WIRE)
            throw new IllegalArgumentException("Trying to check whether a non-wire is connected");

        Direction bFacingDir = bWireBs.get(Properties.HORIZONTAL_FACING);
        Direction bAuxDir = getAuxDirection(bWireBs);

        return bFacingDir == BtoAHorDirection || bAuxDir == BtoAHorDirection;
    }

	/**
	 * Método que que verifica se um fio é possível se conectar
	 * @param world		Mundo que está sendo jogado
	 * @param targetPos		Posição de um bloco alvo
	 * @param issuerPos		Posição do fio de referência
	 * @return		Boolean que indica se o fio pode realizar uma conexão
	 */
    public static boolean isWireConnectable(World world, BlockPos targetPos, BlockPos issuerPos) {
        List<BlockPos> estabilishedConnectionsPos = getWireEstabilishedConnections(world, targetPos);

        //Se tiver menos que duas conexões, pode conectar
        if (estabilishedConnectionsPos.size() < 2) return true;

        //Se o issuer já for uma das conexões, interpreta-se que a conexão é possível
        return estabilishedConnectionsPos.contains(issuerPos);

        //Em qualquer outro caso, o fio target está cheio
    }

	/**
	 * Método que faz updateNeighbor das posições próximas a um fio
	 * se, e somente se, forem um fio. Ex: um fio na diagonal(alturas diferentes)
	 * @param world		Mundo que está sendo jogado
	 * @param mainWirePos		Posição do fio principal no mundo
	 */
    public static void updateUnnaturalNeighborsIfWires(World world, BlockPos mainWirePos) {
        List<BlockPos> otherWiresPos = getWireUncheckedConnections(world, mainWirePos);
        updateUnnaturalNeighborsIfWires(world, mainWirePos, otherWiresPos);
    }

	/**
	 * Método que faz updateNeighbor das posições próximas a um fio
	 * se, e somente se, forem um fio. Ex: um fio na diagonal(alturas diferentes)
	 * @param world		Mundo que está sendo jogado
	 * @param mainWirePos		Posição do fio principal no mundo
	 * @param otherWiresPos		Posição dos outros fios, ligados ao principal
	 */
    public static void updateUnnaturalNeighborsIfWires(World world, BlockPos mainWirePos, List<BlockPos> otherWiresPos) {
        if (otherWiresPos.size() > 0)
            world.updateNeighbor(otherWiresPos.get(0), Blocks.END_PORTAL, mainWirePos);
        if (otherWiresPos.size() > 1)
            world.updateNeighbor(otherWiresPos.get(1), Blocks.END_PORTAL, mainWirePos);
    }

	/**
	 * Método que retorna a posição de um fio procurado na direção fornecida
	 * @param world		Mundo que está sendo jogado
	 * @param mainWirePos		Posição do fio principal
	 * @param directionToGo		Direção a ser seguida
	 * @return		BlockPos do próximo fio
	 */
    public static BlockPos findFirstQuartusBlockFromWireInDirection(World world, BlockPos mainWirePos, Direction directionToGo) {
        return findFirstQuartusBlockFromWireInDirection(world, mainWirePos, directionToGo, true);
    }

	/**
	 * Método que retorna a posição de um fio procurado na direção fornecida
	 * @param world		Mundo que está sendo jogado
	 * @param mainWirePos		Posição do fio principal
	 * @param directionToGo		Direção a ser seguida
	 * @param canGoUp		Boolean que determina se a busca será também realizada no plano acima
	 * @return		BlockPos do próximo fio
	 */
    public static BlockPos findFirstQuartusBlockFromWireInDirection(World world, BlockPos mainWirePos, Direction directionToGo, boolean canGoUp) {
        BlockPos nextComponentPos = mainWirePos.offset(directionToGo);
        BlockPos mainWireTopPos = mainWirePos.offset(Direction.UP);

        //Nível y + 1 (pode ser apenas fio)
        if (canGoUp) {
            if (!world.getBlockState(mainWireTopPos).isSimpleFullBlock(world, mainWireTopPos)) {
                BlockPos upComponentPos = nextComponentPos.offset(Direction.UP);
                if (world.getBlockState(upComponentPos).getBlock() == QuartusBlocks.WIRE) return upComponentPos;
            }
        }

        //Nível y (pode ser fio ou outro componente)
        BlockState nextComponentBs = world.getBlockState(nextComponentPos);
        if (nextComponentBs.getBlock() instanceof QuartusInGameComponent || nextComponentBs.getBlock() == QuartusBlocks.WIRE) return nextComponentPos;

        //Nível y - 1 (pode ser apenas fio)
        if (!nextComponentBs.isSimpleFullBlock(world, nextComponentPos)) {
            nextComponentPos = nextComponentPos.offset(Direction.DOWN);
            nextComponentBs = world.getBlockState(nextComponentPos);
            if (nextComponentBs.getBlock() == QuartusBlocks.WIRE) return nextComponentPos;
        }

        return null;
    }

	/**
	 * Método que retorna a próxima direção a ser seguida (ou optional vazio se a aproximação tiver sido feita por uma direção inválida)
	 * @param world		Mundo que está sendo jogado
	 * @param mainWirePos		Posição do fio principal
	 * @param lastDirection		Última direção seguida
	 * @return		Direção a ser seguida
	 */
    public static Optional<Direction> getNextDirection(World world, BlockPos mainWirePos, Direction lastDirection) {
		//TODO: colocar no wire
        BlockState mainWireBs = world.getBlockState(mainWirePos);
        if (mainWireBs.getBlock() != QuartusBlocks.WIRE) throw new IllegalArgumentException("Trying to navigate a non-wire block");

        Direction facingDirection = mainWireBs.get(Properties.HORIZONTAL_FACING);
        Direction auxDirection = getAuxDirection(mainWireBs);

        if (lastDirection.getOpposite() == facingDirection) return Optional.of(auxDirection);
        else if (lastDirection.getOpposite() == auxDirection) return Optional.of(facingDirection);
        else {
            Quartus.LOGGER.warn("Invalid approaching direction (going to " + lastDirection + ") at " + mainWirePos);
            return Optional.empty();
        }
    }

	/**
	 * Método que retorna um BlockPos do próximo fio a ser navegado
	 * @param world		Mundo que está sendo jogado
	 * @param mainWirePos		Posição do fio principal
	 * @param directionToGo		Direção a ser seguida
	 * @return		BlockPos do próximo fio a ser navegado
	 */
    @Nullable
    public static BlockPos navigateWire(World world, BlockPos mainWirePos, Direction directionToGo) {
        BlockState mainWireBs = world.getBlockState(mainWirePos);
        if (mainWireBs.getBlock() != QuartusBlocks.WIRE) throw new IllegalArgumentException("Trying to navigate a non-wire block");

        UpValue upValue = mainWireBs.get(QuartusProperties.WIRE_UP);
        Direction facingDir = mainWireBs.get(Properties.HORIZONTAL_FACING);
        Direction auxDir = getAuxDirection(mainWireBs);
        if (directionToGo != facingDir && directionToGo != auxDir) return null;

        boolean canGoUp = upValue == UpValue.BOTH || (upValue == UpValue.FACING && facingDir == directionToGo);
        return findFirstQuartusBlockFromWireInDirection(world, mainWirePos, directionToGo, canGoUp);
    }

	/**
	 * Método que retorna uma lista com BlockPos dos blocos encontrados
	 * que podem ser conectados
	 * @param world		Mundo que está sendo jogado
	 * @param mainWirePos		Posição do fio principal
	 * @param existentConnectionsPos		Lista com BlockPos dos blocos que podem ser conectados
	 * @param maxCount		Número de conexões restantes possíveis
	 * @return		Lsita com BlockPos dos blocos que podem ser encontrados
	 */
    public static List<BlockPos> findConnectableQuartusBlocks(World world, BlockPos mainWirePos, List<BlockPos> existentConnectionsPos, int maxCount) {
        List<BlockPos> connectionsPos = new ArrayList<>();

        for (Direction horizontalDir: DirectionUtils.HORIZONTAL_DIRECTIONS) {
            BlockPos foundBlockPos = findFirstQuartusBlockFromWireInDirection(world, mainWirePos, horizontalDir);

            //Se bloco encontrado já for uma conexão existente, procure outra
            if (existentConnectionsPos.contains(foundBlockPos)) continue;

            if (foundBlockPos == null) continue;

            BlockState foundBlockState = world.getBlockState(foundBlockPos);
            if (foundBlockState.getBlock() instanceof QuartusInGameComponent) {
                if (!ComponentUtils.isComponentConnectableAtDirection(world, foundBlockPos, horizontalDir.getOpposite())) continue;
            } else if (foundBlockState.getBlock() == QuartusBlocks.WIRE) {
                if (!isWireConnectable(world, foundBlockPos, mainWirePos)) continue;
            }

            connectionsPos.add(foundBlockPos);
            if (connectionsPos.size() >= maxCount) break;
        }

        return connectionsPos;
    }
}
