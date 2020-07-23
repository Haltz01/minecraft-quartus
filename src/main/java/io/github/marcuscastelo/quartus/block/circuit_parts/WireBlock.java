package io.github.marcuscastelo.quartus.block.circuit_parts;

import io.github.marcuscastelo.quartus.block.QuartusTransportInfoProvider;
import io.github.marcuscastelo.quartus.registry.QuartusProperties;
import io.github.marcuscastelo.quartus.util.WireConnector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WireBlock extends HorizontalFacingBlock implements QuartusTransportInfoProvider {
    public enum UpValues implements StringIdentifiable {
        NONE("none"), FACING("facing"), BOTH("both");

        String identifier;

        UpValues(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public String asString() {
            return identifier;
        }
    }

    private static final BooleanProperty TURN = QuartusProperties.WIRE_TURN;
    private static final BooleanProperty POSITIVE = QuartusProperties.WIRE_POSITIVE;
    private static final EnumProperty<UpValues> UP = QuartusProperties.WIRE_UP;

    public WireBlock() {
        super(Settings.copy(Blocks.REPEATER));
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(TURN, false).with(POSITIVE, false).with(UP, UpValues.NONE));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.updateNeighbor(pos, state.getBlock(), pos);
    }


    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos currPos, Block block, BlockPos neighborPos, boolean moved) {
        //O processamento é feito apenas no servidor
        if (world.isClient) return;

        //Se o fio atual já possui duas conexões, não precisa atualizar
        List<BlockPos> alreadyEstabilishedConnections = WireConnector.getWireEstabilishedConnections(world, currPos);
        if (alreadyEstabilishedConnections.size() == 2) return;

        int freeConnectionSlotsCount = 2 - alreadyEstabilishedConnections.size();

        List<BlockPos> newConnectionList = new ArrayList<>();
        newConnectionList.addAll(alreadyEstabilishedConnections);
        newConnectionList.addAll(WireConnector.findConnectableQuartusBlocks(world, currPos, alreadyEstabilishedConnections, freeConnectionSlotsCount));

        WireConnector.connectTo(world, currPos, newConnectionList);

        //Atualiza os fios que podem estar em baixo ou em cima para detectar a existência desse novo fio (a não ser que isso já tenha acontecido: flag END_PORTAL)
        if (block != Blocks.END_PORTAL){
            WireConnector.updateUnnaturalNeighborsIfWires(world, currPos);
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState bottomBlockstate = world.getBlockState(pos.offset(Direction.DOWN));
        return bottomBlockstate.isSideSolidFullSquare(world,pos,Direction.UP);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        return canPlaceAt(state, world, pos)? state: Blocks.AIR.getDefaultState();
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        return Arrays.asList(new ItemStack(state.getBlock().asItem()));
    }


    //TODO: arrumar de acordo com as novas mudanças
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        if (state.get(TURN)) {
            //Adds center part
            VoxelShape shape = VoxelShapes.cuboid(6/16f, 0, 6/16f, 10/16f, 2/16f, 10/16f);
            Direction d = state.get(FACING);
            if (true) return shape;

            if (d == Direction.NORTH) {
                //North
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(6/16f, 0, 0, 10/16f, 2/16f, 6/16f));
                //West
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 6/16f, 6/16f, 2/16f, 10/16f));
            }
            else if (d == Direction.SOUTH) {
                //South
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(6/16f, 0, 10/16f, 10/16f, 2/16f, 1f));
                //East
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(10/16f, 0, 6/16f, 1f, 2/16f, 10/16f));
            }
            else if (d == Direction.WEST) {
                //West
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 6/16f, 6/16f, 2/16f, 10/16f));
                //South
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(6/16f, 0, 10/16f, 10/16f, 2/16f, 1f));
            } else {
                //East
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(10/16f, 0, 6/16f, 1f, 2/16f, 10/16f));
                //North
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(6/16f, 0, 0, 10/16f, 2/16f, 6/16f));
            }
            return shape;

        }
        else if (state.get(FACING) == Direction.NORTH || state.get(FACING) == Direction.SOUTH)
            return VoxelShapes.cuboid(6/16f, 0.0f, 0f, 10/16f, 2/16f, 1f);
        else
            return VoxelShapes.cuboid(0, 0.0f, 6/16f, 1f, 2/16f, 10/16f);
    }

    @Override
    public void onBlockRemoved(BlockState oldState, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved) return;
        if (newState.getBlock() == this) return;
        int flagToAvoidEndlessLoop = 64 | 2;

        world.setBlockState(pos, oldState, flagToAvoidEndlessLoop);
        List<BlockPos> oldConnections = WireConnector.getWireEstabilishedConnections(world, pos);
        world.setBlockState(pos, newState, flagToAvoidEndlessLoop);
        WireConnector.updateUnnaturalNeighborsIfWires(world, pos, oldConnections);
        System.out.println(oldConnections);
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TURN, POSITIVE, UP, FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing());
    }

    @Override
    public Direction nextDirection(World world, BlockPos pos, Direction facingBefore) {
        //TODO: implementar

        return Direction.NORTH;
    }
}
