package io.github.marcuscastelo.quartus.blockentity;

import io.github.marcuscastelo.quartus.registry.QuartusItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;

/**
 * A simple {@code Inventory} implementation with only default methods + an item list getter.
 *
 * Originally by Juuz
 * Traduzido e completado para o português
 */
public interface ImplementedInventory extends Inventory {
    /*
      Gets the item list of this inventory.
      Must return the same instance every time it's called.
     */
	/**
	 * Recebe a lista de itens do inventário.
	 * Deve retornar a mesma instânciação a toda chamada
	 */
    DefaultedList<ItemStack> getItems();
    // Creation
    /*
      Creates an inventory from the item list.
     */
	/**
	 * Cria um inventário a partir da lista de itens
	 */
    static ImplementedInventory of(DefaultedList<ItemStack> items) {
        return () -> items;
    }
    /*
      Creates a new inventory with the size.
     */
	/**
	 * Cria um novo inventário com o tamanho passado como parâmetro
	 * @return		Lista padrão vazia com o tamanho fornecido
	 */
    static ImplementedInventory ofSize(int size) {
        return of(DefaultedList.ofSize(size, ItemStack.EMPTY));
    }
    // Inventory
    /*
      Returns the inventory size.
     */
	/**
	 * Retorna o tamanho do inventário
	 * @return		Tamanho do inventário
	 */
    @Override
    default int getInvSize() {
        return getItems().size();
    }
    /*
      @return true if this inventory has only empty stacks, false otherwise
     */
	/**
	 * @return verdadeiro se o inventário possui apenas pilhas/posições vazias, false caso contrário
	 */
    @Override
    default boolean isInvEmpty() {
        for (int i = 0; i < getInvSize(); i++) {
            ItemStack stack = getInvStack(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    /*
      Gets the item in the slot.
     */
	/**
	 * Retorna o item da posição fornecida
	 * @return		Item na posição dada
	 */
    @Override
    default ItemStack getInvStack(int slot) {
        return getItems().get(slot);
    }
    /*
      Takes a stack of the size from the slot.
      <p>(default implementation) If there are less items in the slot than what are requested,
      takes all items in that slot.
     */
	/**
	 * Toma uma pilha, do tamanho passado como parâmetro, da posição
	 * Implementação padrão: Se há menos itens na posição do que o necessário,
	 * toma todos os itens da posição
	 * @return		Pilha de itens com o tamanho fornecido (ou o máximo possível)
	 */
    @Override
    default ItemStack takeInvStack(int slot, int count) {
        ItemStack result = Inventories.splitStack(getItems(), slot, count);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }
    /*
      Removes the current stack in the {@code slot} and returns it.
     */
	/**
	 * Remove a pilha atual na posição slot e retorna
	 * @return		Pilha de itens removida
	 */
    @Override
    default ItemStack removeInvStack(int slot) {
        return Inventories.removeStack(getItems(), slot);
    }
    /*
      Replaces the current stack in the {@code slot} with the provided stack.
      <p>If the stack is too big for this inventory ({@link Inventory#getInvMaxStackAmount()}),
      it gets resized to this inventory's maximum amount.
     */
	/**
	 * Troca de posições a pilha na posição slot com uma pilha fornecida
	 * Se a pilha é grande demais para o inventário,
	 * é redimensionada para o tamanho máximo do inventário
	 */
    @Override
    default void setInvStack(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        if (stack.getCount() > getInvMaxStackAmount()) {
            stack.setCount(getInvMaxStackAmount());
        }
    }
    /*
      Clears {@linkplain #getItems() the item list}}.
     */
	/**
	 * Esvazia a lista de itens
	 */
    @Override
    default void clear() {
        getItems().clear();
    }
    @Override
    default void markDirty() {

	}
	/**
	 * Método que retorna um boolean que confirma a permissão de uso do inventário pelo usuário
	 * @param player		Jogador
	 * @return		Boolean de confirmação
	 */
    @Override
    default boolean canPlayerUseInv(PlayerEntity player) {
        return true;
    }

	/**
	 * Método que verifica se o item na posição slot é válida
	 * Apenas utilizada no Compiler e no Executor
	 * Retorna verdadeiro se for um FloppyDisk (disquete)
	 * Caso seja falso, não sera possível colocar o item
	 * @param slot		Posição do item no inventário
	 * @param stack		Pilha de itens
	 * @return		Boolean de confirmação
	 */
    @Override
    default boolean isValidInvStack(int slot, ItemStack stack) {
        return stack.getItem().equals(QuartusItems.FLOPPY_DISK);
    }

	/**
	 * Método que define o tamanho máximo de uma pilha no inventário
	 * @return		1
	 */
    @Override
    default int getInvMaxStackAmount() {
        return 1;
    }
}