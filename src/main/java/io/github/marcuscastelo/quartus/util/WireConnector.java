package io.github.marcuscastelo.quartus.util;

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
import java.util.stream.Collectors;

public class WireConnector {
    private enum UpValuesDirections { NONE, FIRST, SECOND, BOTH };

    private static boolean hasWireChanged(BlockState oldState, BlockState newState) {
        return oldState.get(Properties.HORIZONTAL_FACING) != newState.get(Properties.HORIZONTAL_FACING) ||
                oldState.get(QuartusProperties.WIRE_TURN) != newState.get(QuartusProperties.WIRE_TURN) ||
                oldState.get(QuartusProperties.WIRE_POSITIVE) != newState.get(QuartusProperties.WIRE_POSITIVE) ||
                oldState.get(QuartusProperties.WIRE_UP) != newState.get(QuartusProperties.WIRE_UP);
    }

    public static boolean isConnectionTurned(Direction facingDirection, @Nullable Direction auxDirection) {
        return auxDirection != null && !facingDirection.getOpposite().equals(auxDirection);
    }

    public static boolean isConnectionPositive(Direction facingDirection, @Nullable Direction auxDirection) {
        if (auxDirection == null) return false;
        return facingDirection.rotateYCounterclockwise().equals(auxDirection);
    }

    //Retorna duas direções (sem compromiso de facing ou aux)
    public static Pair<Direction, Direction> getConnectionHorizontalDirections(BlockPos mainWirePos, List<BlockPos> otherBlocksPos) {
        if (otherBlocksPos.size() > 2) throw new UnsupportedOperationException("Não é possível conectar um fio a mais de 2");

        Direction direction1 = null, direction2 = null;
        if (otherBlocksPos.size() > 0)
            direction1 = DirectionUtils.getHorizontalDirectionAtoB(mainWirePos, otherBlocksPos.get(0));
        if (otherBlocksPos.size() > 1)
            direction2 = DirectionUtils.getHorizontalDirectionAtoB(mainWirePos, otherBlocksPos.get(1));

        return new Pair<>(direction1, direction2);

    }

    public static Pair<Direction, Direction> getConnectionVerticalDirections(BlockPos mainWirePos, List<BlockPos> otherBlocksPos) {
        if (otherBlocksPos.size() > 2) throw new UnsupportedOperationException("Não é possível conectar um fio a mais de 2");

        Direction direction1 = null, direction2 = null;
        if (otherBlocksPos.size() > 0)
            direction1 = DirectionUtils.getVerticalDirectionAtoB(mainWirePos, otherBlocksPos.get(0));
        if (otherBlocksPos.size() > 1)
            direction2 = DirectionUtils.getVerticalDirectionAtoB(mainWirePos, otherBlocksPos.get(1));

        return new Pair<>(direction1, direction2);
    }

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
        WireBlock.UpValues newStateUpValues;
        Direction newFacingDirection = horizontalDirections.getLeft();
        Direction newAuxDirection = horizontalDirections.getRight();
        boolean newTurn = isConnectionTurned(newFacingDirection, newAuxDirection);
        boolean newPositive = isConnectionPositive(newFacingDirection, newAuxDirection);

        EnumSet<UpValuesDirections> upValuesDirections = getConnectionUpValuesInfo(verticalDirections);
        if (upValuesDirections.contains(UpValuesDirections.BOTH)) {
            newStateUpValues = WireBlock.UpValues.BOTH;
        } else if (upValuesDirections.contains(UpValuesDirections.SECOND)) {
            newStateUpValues = WireBlock.UpValues.FACING;
            System.out.println("ELE MESMO !!");
            Direction tempDirForSwap = newFacingDirection;
            //Troca a direção principal com a direção auxiliar (para manter o turn certo, muda a posividade)
            if (newTurn) newPositive = !newPositive;
            newFacingDirection = newAuxDirection;
            newAuxDirection = tempDirForSwap;

        } else if (upValuesDirections.contains(UpValuesDirections.FIRST)) {
            newStateUpValues = WireBlock.UpValues.FACING;
        } else {
            newStateUpValues = WireBlock.UpValues.NONE;
        }

        newState = oldState.with(Properties.HORIZONTAL_FACING, newFacingDirection)
                .with(QuartusProperties.WIRE_UP, newStateUpValues)
                .with(QuartusProperties.WIRE_POSITIVE, newPositive)
                .with(QuartusProperties.WIRE_TURN, newTurn);

        //Passo 5:
        world.setBlockState(mainWirePos, newState, 2);

        //Passo 6:
        if (hasWireChanged(oldState, newState)) {
            updateUnnaturalNeighborsIfWires(world, mainWirePos);
        }
    }

    public static Direction getAuxDirection(BlockState wireBlockState) {
        if (wireBlockState.getBlock() != QuartusBlocks.WIRE) throw new IllegalArgumentException("Blockstate must be a wire blockstate");
        return getAuxDirection(wireBlockState.get(Properties.HORIZONTAL_FACING), wireBlockState.get(QuartusProperties.WIRE_TURN), wireBlockState.get(QuartusProperties.WIRE_POSITIVE));
    }


    public static Direction getAuxDirection(Direction facingDirection, boolean turned, boolean positive) {
        if (!turned) return facingDirection.getOpposite();

        if (positive) return facingDirection.rotateYCounterclockwise();
        else return facingDirection.rotateYClockwise();
    }

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

    private static boolean isWire(World world, BlockPos pos) { return world.getBlockState(pos).getBlock() == QuartusBlocks.WIRE; }

    public static List<BlockPos> getWireEstabilishedConnections(World world, BlockPos mainWirePos) {
        return getWireUncheckedConnections(world, mainWirePos).stream().filter(connectionPos -> !isWire(world,connectionPos) || areWiresConnected(world, mainWirePos, connectionPos)).collect(Collectors.toList());
    }


    //Verifica se ambos os fios apontam um para o outro
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

    public static boolean isWireConnectable(World world, BlockPos targetPos, BlockPos issuerPos) {
        List<BlockPos> estabilishedConnectionsPos = getWireEstabilishedConnections(world, targetPos);

        //Se tiver menos que duas conexões, pode conectar
        if (estabilishedConnectionsPos.size() < 2) return true;

        //Se o issuer já for uma das conexões, interpreta-se que a conexão é possível
        if (estabilishedConnectionsPos.contains(issuerPos)) return true;

        //Em qualquer outro caso, o fio target está cheio
        return false;
    }

    public static void updateUnnaturalNeighborsIfWires(World world, BlockPos mainWirePos) {
        List<BlockPos> otherWiresPos = getWireUncheckedConnections(world, mainWirePos);
        updateUnnaturalNeighborsIfWires(world, mainWirePos, otherWiresPos);
    }

    public static void updateUnnaturalNeighborsIfWires(World world, BlockPos mainWirePos, List<BlockPos> otherWiresPos) {
        Pair<Direction, Direction> verticalDirections = getConnectionVerticalDirections(mainWirePos, otherWiresPos);

        //Se o wire não estiver no mesmo nível, atualize
        if (verticalDirections.getLeft() != null)
            world.updateNeighbor(otherWiresPos.get(0), Blocks.END_PORTAL, mainWirePos);
        if (verticalDirections.getRight() != null)
            world.updateNeighbor(otherWiresPos.get(1), Blocks.END_PORTAL, mainWirePos);
    }

    public static BlockPos findFirstQuartusBlockFromWireInDirection(World world, BlockPos mainWirePos, Direction directionToGo) {
        return findFirstQuartusBlockFromWireInDirection(world, mainWirePos, directionToGo, true);
    }

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

    //TODO: colocar no wire
    @Nullable
    public static Direction getNextDirection(World world, BlockPos mainWirePos, Direction lastDirection) {
        BlockState mainWireBs = world.getBlockState(mainWirePos);
        if (mainWireBs.getBlock() != QuartusBlocks.WIRE) throw new IllegalArgumentException("Trying to navigate a non-wire block");

        Direction facingDirection = mainWireBs.get(Properties.HORIZONTAL_FACING);
        Direction auxDirection = getAuxDirection(mainWireBs);

        if (lastDirection == facingDirection) return auxDirection;
        else if (lastDirection == auxDirection) return facingDirection;
        else return null;
    }

    @Nullable
    public static BlockPos navigateWire(World world, BlockPos mainWirePos, Direction directionToGo) {
        BlockState mainWireBs = world.getBlockState(mainWirePos);
        if (mainWireBs.getBlock() != QuartusBlocks.WIRE) throw new IllegalArgumentException("Trying to navigate a non-wire block");

        WireBlock.UpValues upValues = mainWireBs.get(QuartusProperties.WIRE_UP);
        Direction facingDir = mainWireBs.get(Properties.HORIZONTAL_FACING);
        Direction auxDir = getAuxDirection(mainWireBs);
        if (directionToGo != facingDir && directionToGo != auxDir) return null;

        boolean canGoUp = upValues == WireBlock.UpValues.BOTH || (upValues == WireBlock.UpValues.FACING && facingDir == directionToGo);
        return findFirstQuartusBlockFromWireInDirection(world, mainWirePos, directionToGo, canGoUp);
    }

    public static List<BlockPos> findConnectableQuartusBlocks(World world, BlockPos mainWirePos, int maxCount) {
        List<BlockPos> connectionsPos = new ArrayList<>();

        for (Direction horizontalDir: DirectionUtils.HORIZONTAL_DIRECTIONS) {
            BlockPos foundBlockPos = findFirstQuartusBlockFromWireInDirection(world, mainWirePos, horizontalDir);
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
