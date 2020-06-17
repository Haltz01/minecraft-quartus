package io.github.marcuscastelo.quartus.block;

import io.github.marcuscastelo.quartus.registry.QuartusItems;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ExtensorIOBlock extends HorizontalFacingBlock {
    public enum ExtensorIOState implements StringIdentifiable {
        VOID("void"), VOID_END("void_end"), INPUT("input"), OUPUT("output"), IO("io");

        String identifier;
        ExtensorIOState(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public String asString() {
            return identifier;
        }
    }

    public static final EnumProperty<ExtensorIOState> EXTENSOR_STATE = EnumProperty.of("extensor_state", ExtensorIOState.class);

    public ExtensorIOBlock() {
        super(Settings.of(Material.PART).nonOpaque());
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(EXTENSOR_STATE, ExtensorIOState.VOID));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, EXTENSOR_STATE);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction futureFacing = ctx.getPlayerFacing().getOpposite();
        Direction left;
        left = futureFacing.rotateYClockwise();

        World world = ctx.getWorld();

        BlockState leftBlockState = world.getBlockState(ctx.getBlockPos().offset(left));

        if (!(leftBlockState.getBlock() instanceof HorizontalFacingBlock) || leftBlockState.get(FACING) != futureFacing || (!leftBlockState.getBlock().equals(this)  && !(leftBlockState.getBlock() instanceof ExecutorBlock))) return Blocks.AIR.getDefaultState();
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite()).with(EXTENSOR_STATE, ExtensorIOState.VOID_END);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
        ExtensorIOState extensorIOState = state.get(EXTENSOR_STATE);
        Direction facingDirection = world.getBlockState(pos).get(FACING);

        BlockState rightBlockState = world.getBlockState(pos.offset(facingDirection.rotateYClockwise()));
        if (!rightBlockState.getBlock().equals(this) && !(rightBlockState.getBlock() instanceof ExecutorBlock)) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            dropStack(world, pos, new ItemStack(QuartusItems.EXTENSOR_IO, 1));
            return;
        }

        if (extensorIOState == ExtensorIOState.VOID_END || extensorIOState == ExtensorIOState.VOID) {
            BlockState neighborState = world.getBlockState(pos.offset(facingDirection.rotateYCounterclockwise()));
            if (neighborState.getBlock().equals(this) && neighborState.get(FACING) == facingDirection) {
                world.setBlockState(pos, state.with(EXTENSOR_STATE, ExtensorIOState.VOID));
            } else {
                world.setBlockState(pos, state.with(EXTENSOR_STATE, ExtensorIOState.VOID_END));
            }
            return;
        }
    }
}