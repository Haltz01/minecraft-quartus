package io.github.marcuscastelo.quartus.item;

import io.github.marcuscastelo.quartus.Quartus;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.time.format.TextStyle;
import java.util.Arrays;
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
	 * Método que cria uma tooltip no item de acordo com a sua tag circuit
	 * @param stack		Pilha que contém uma lista de items guardados no inventario de um bloco
	 * @param world		Mundo que está sendo jogado
	 * @param tooltip		Dica flutuante
	 * @param context		Contexto da tooltip
	 */
	//TODO: TESTAR!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11!ONZE
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		String circuitStr = stack.getOrCreateTag().getString("circuit");
		if (circuitStr.length() == 0) {
			tooltip.add(new TranslatableText("quartus.item.floppy_disk.empty"));
			return;
		}

		String[] lines = circuitStr.split("\n");
		if (lines.length > 8) {
			tooltip.add(new TranslatableText("quartus.item.floppy_disk.circuit_too_big"));
			return;
		}

		Arrays.stream(lines).forEach(line -> {
			tooltip.add(new LiteralText(line));
		});
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
