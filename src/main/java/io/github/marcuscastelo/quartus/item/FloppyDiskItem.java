package io.github.marcuscastelo.quartus.item;

import io.github.marcuscastelo.quartus.Quartus;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

/**
 * Classe que define o item FloppyDisk (disquete),
 * que armazena um circuito montado pelo jogador
 */
public class FloppyDiskItem extends Item {
	/**
	 * Construtor padrão da classe FloppyDiskItem
	 */
    public FloppyDiskItem() {
        super(new Settings().group(Quartus.ITEMGROUP));
    }

	/**
	 * Método que verifica se há um circuito dentro de um FloppyDisk que o usuário está segurando
	 * Caso o FloppyDisk esteja corrompido, vazio, não faz nada
	 * Caso esteja com um circuito dentro, o jogador faz a ação de
	 * mecher a mão e printa o cirtuito serializado
	 * @param world		Mundo que está sendo jogado
	 * @param user		Entitade do usuário (jogador)
	 * @param hand		Mão utlizada pelo jogador
	 */
    @Override
    @Environment(EnvType.CLIENT)
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!user.world.isClient || MinecraftClient.getInstance().player == null) return TypedActionResult.pass(stack);

        if (stack.getTag() == null || !stack.getTag().contains("circuit")) return TypedActionResult.pass(stack);
        MinecraftClient.getInstance().player.sendMessage(new LiteralText(stack.getOrCreateTag().getString("circuit")));
        
        return TypedActionResult.success(stack);
    }

	/**
	 * Método que cria e/ou atribui uma tag "circuit" ao item
	 * @param stack		Pilha que contém uma lista de items guardados no inventario de um bloco
	 * @param world		Mundo que está sendo jogado
	 * @param tooltip		Dica flutuante
	 * @param context		Contexto da tooltip
	 */
	//TODO: TESTAR!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11!ONZE
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        stack.getOrCreateTag().get("circuit");
    }

	/**
	 * Método que retorna um boolean caso o FloppyDiskItem tenha
	 * a propriedade de brilho (glint), indicando que possui
	 * um circuito dentro
	 * @param stack		Pilha que contém uma lista de items guardados no inventario de um bloco
	 */
    @Override
    public boolean hasEnchantmentGlint(ItemStack stack) {
        return stack.getTag() != null && stack.getTag().contains("circuit");
    }
}
