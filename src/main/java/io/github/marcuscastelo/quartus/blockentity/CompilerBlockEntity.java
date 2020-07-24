package io.github.marcuscastelo.quartus.blockentity;

import io.github.marcuscastelo.quartus.registry.QuartusBlockEntities;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;

/**
 * Classe que define o BlocoEntity do Compiler
 * BlockEntity guarda dados dentro de um bloco ao qual ele foi atribuído
 */
public class CompilerBlockEntity extends BlockEntity implements ImplementedInventory {
	//Variável que armazena os itens dentro do bloco
	DefaultedList<ItemStack> inventoryItems;
	
	/**
	 * Construtor padrão da classe CompileBlockEntity
	 */
    public CompilerBlockEntity() {
        super(QuartusBlockEntities.COMPILER_BLOCK_ENTITY_TYPE);
        inventoryItems = DefaultedList.ofSize(1, ItemStack.EMPTY);
    }

	/**
	 * Método auxiliar que retorna uma lista com os itens do bloco
	 */
    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventoryItems;
    }

	/**
	 * Método que atribui os itens e o bloco a uma tag e retorna a tag (informações) do bloco
	 * @param tag		Tag utilizada
	 * @return		Tag/Dados
	 */
    @Override
    public CompoundTag toTag(CompoundTag tag) {
        Inventories.toTag(tag, inventoryItems);
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
    }
}
