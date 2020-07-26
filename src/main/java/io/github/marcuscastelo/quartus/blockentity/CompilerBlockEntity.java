package io.github.marcuscastelo.quartus.blockentity;

import io.github.marcuscastelo.quartus.registry.QuartusBlockEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Classe que define o BlocoEntity do Compiler
 * BlockEntity guarda dados dentro de um bloco ao qual ele foi atribuído
 */
public class CompilerBlockEntity extends BlockEntity implements ImplementedInventory, Tickable, BlockEntityClientSerializable {
	//Variável que armazena os itens dentro do bloco
	private final DefaultedList<ItemStack> inventoryItems;
	private int compilingAreaSize;
	public static int MAX_COMPILING_AREA_SIZE = 99;
	
	/**
	 * Construtor padrão da classe CompileBlockEntity
	 */
    public CompilerBlockEntity() {
        super(QuartusBlockEntities.COMPILER_BLOCK_ENTITY_TYPE);
        inventoryItems = DefaultedList.ofSize(1, ItemStack.EMPTY);
        compilingAreaSize = 10;

    }

	/**
	 * Método que retorna uma lista com os itens do bloco
	 */
    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventoryItems;
    }

    public int getCompilingAreaSize() {
        return compilingAreaSize;
    }

    public void setCompilingAreaSize(int compilingAreaSize) {
        this.compilingAreaSize = MathHelper.clamp(compilingAreaSize, 1, MAX_COMPILING_AREA_SIZE);
    }

    /**
	 * Método que atribui os itens e o bloco a uma tag e retorna a tag (informações) do bloco
	 * @param tag		Tag utilizada
	 * @return		Tag/Dados
	 */
    @Override
    public CompoundTag toTag(CompoundTag tag) {
        Inventories.toTag(tag, inventoryItems);
        tag.putInt("compiler_size", compilingAreaSize);
        return super.toTag(tag);
    }

	/**
	 * Método que recebe os itens e o bloco de uma tag passada, sem retorno
	 * @param tag		Tag utilizada
	 */
    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        Inventories.fromTag(tag, inventoryItems);
        compilingAreaSize = tag.getInt("compiler_size");
    }

    private final Random random = new Random();
    private int ignoredTicks = 0;
    @Override
    public void tick() {
        ignoredTicks++;
        if (random.nextFloat() <= 095f && ignoredTicks <= 20) return;
        if (world == null || !world.isClient) return;
        ignoredTicks = 0;
        outlineCompileRegionForClient(world,pos);
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        fromTag(compoundTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return toTag(compoundTag);
    }

    private void setParticleAt(World world, BlockPos particlePos) {
        world.addParticle(new DustParticleEffect(random.nextFloat(), 0, 0, 7), particlePos.getX()+0.5f, particlePos.getY()+0.5f, particlePos.getZ()+0.5f, 0, 0, 0);
    }

    @Environment(EnvType.CLIENT)
    public void outlineCompileRegionForClient(World world, BlockPos compilerPos) {
        if (!world.isClient) return;

        BlockEntity be = world.getBlockEntity(compilerPos);
        if (!(be instanceof CompilerBlockEntity)) return;
        int innerSize = ((CompilerBlockEntity) be).getCompilingAreaSize();

        BlockState compilerBs = world.getBlockState(compilerPos);
        Direction front = compilerBs.get(Properties.HORIZONTAL_FACING).getOpposite();
        Direction right = front.rotateYClockwise();
        Direction left = front.rotateYCounterclockwise();
        Direction back = front.getOpposite();

        int blockStep = random.nextInt(2) + 1;

        //Regulariza o tamanho, se for par, torna-se o primeiro menor impar
        innerSize -= (innerSize+1)%2;

        BlockPos middleCorner1 = compilerPos.offset(right, innerSize/2 + 1);
        BlockPos middleCorner2 = middleCorner1.offset(front, innerSize + 1);
        BlockPos middleCorner3 = middleCorner2.offset(left, innerSize + 1);
        BlockPos middleCorner4 = middleCorner3.offset(back, innerSize+1);

        for (int i = 1; i <= innerSize; i+=blockStep) {
            setParticleAt(world, middleCorner1.offset(front, i));
            setParticleAt(world, middleCorner2.offset(left, i));
            setParticleAt(world, middleCorner3.offset(back, i));
            setParticleAt(world, middleCorner4.offset(right, i));
        }
    }
}
