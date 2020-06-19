package io.github.marcuscastelo.quartus.block;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.blockentity.CompilerBlockEntity;
import io.github.marcuscastelo.quartus.circuit_logic.CircuitUtils;
import io.github.marcuscastelo.quartus.registry.QuartusCottonGUIs;
import io.github.marcuscastelo.quartus.registry.QuartusItems;
import jdk.internal.jline.internal.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CompilerBlock extends HorizontalFacingBlock implements BlockEntityProvider {
    public CompilerBlock() {
        super(Settings.copy(Blocks.ANVIL));
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new CompilerBlockEntity();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        ContainerProviderRegistry.INSTANCE.openContainer(Quartus.id("compiler"), player, packetByteBuf -> packetByteBuf.writeBlockPos(pos));

        return ActionResult.SUCCESS;
    }

    private void handleBlockTagOnPlace(World world, BlockPos pos, ItemStack compilerIS) {
        if (compilerIS.getTag() == null) return;
        if (!compilerIS.getTag().contains("hasFloppy")) return;

        if (!compilerIS.getTag().getBoolean("hasFloppy")) return;

        Inventory inv = QuartusCottonGUIs.getBlockInventory(world, pos);
        ItemStack floppyIS = new ItemStack(QuartusItems.FLOPPY_DISK, 1);
        if (compilerIS.getTag().contains("floppyTag"))
            floppyIS.setTag(compilerIS.getTag().getCompound("floppyTag"));

        assert inv != null;
        inv.setInvStack(0, floppyIS);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack compilerIS) {
        handleBlockTagOnPlace(world, pos, compilerIS);
        CircuitUtils.outlineCompileRegionForClient(world, pos, 10, Blocks.DIRT);
    }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        //TODO: make it work
        CircuitUtils.outlineCompileRegionForClient(world, pos, 10, Blocks.COAL_BLOCK);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        if (!(world instanceof World)) {
            return new ItemStack(this);
        }
        Inventory inv = QuartusCottonGUIs.getBlockInventory((World)world, pos);
        ItemStack floppyIS = inv.getInvStack(0);

        if (floppyIS.isEmpty()) return new ItemStack(this);

        ItemStack compilerIS = new ItemStack(this, 1);
        CompoundTag floppyTag = floppyIS.getTag();
        compilerIS.getOrCreateTag().putBoolean("hasFloppy", true);
        if (floppyTag == null) return compilerIS;

        compilerIS.getOrCreateTag().put("floppyTag", floppyTag);
        return compilerIS;
    }
}
