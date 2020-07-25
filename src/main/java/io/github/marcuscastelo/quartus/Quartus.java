package io.github.marcuscastelo.quartus;

import io.github.marcuscastelo.quartus.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Classe que implementa a interface ModInitializer (do FabricMC), fornece um método onInitialize
 * que inicializa os módulos do mod.
 */
public class Quartus implements ModInitializer {
    //TODO: adicionar receitas ao livro de crafting

	//Prefixo utilizado para qualquer recurso do mod que necessite ser registrado no jogo (blocos, itens, controladores de inventário, etc.).
    public static final String MOD_ID = "quartus";

    //Instância de uma estrutura de logging de erros, informações de debug e warnings.
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    /**
     *  No menu criativo, os itens estão separados por categorias - ou grupos de itens.
     *  Este campo armazena o grupo de itens do mod (tanto itens puros quanto itens de blocos).
     *  OBS: a ordem dos itens no grupo é dada pela função @QuartusItems::appendItemGroupStacksInOrder
     *  @see QuartusItems#appendItemGroupStacksInOrder(List lista a ser preenchida na ordem)
     */
    public static ItemGroup ITEMGROUP =
            FabricItemGroupBuilder.create(id("item_group"))
                    .icon(()->new ItemStack(QuartusItems.FLOPPY_DISK))
                    .appendItems(QuartusItems::appendItemGroupStacksInOrder)
                    .build();
	/**
	 * Método principal de inicialização do mod, inicia blocos, entidades de blocos, itens e outros recursos do mod.
	 */
    @Override
    public void onInitialize() {
        System.out.println("onInitialize()");

        QuartusBlocks.init();
        QuartusItems.init();
        QuartusBlockEntities.init();
        QuartusNetworkHandlers.init();
        QuartusCottonControllers.init();

        LOGGER.info("[Quartus] Server ready!");
    }

	/**
	 * Método que auxilia na criação de um identificador id para os blocos desenvolvidos
	 * @param name		nome passado como sufixo para identificador de um bloco
	 * @return		retorna o identificador gerado a partir do sufixo passado (name) e MOD_ID
	 */
    public static Identifier id(String name) {
        return new Identifier(MOD_ID, name);
    }
}
