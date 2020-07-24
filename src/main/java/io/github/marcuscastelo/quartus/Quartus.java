package io.github.marcuscastelo.quartus;

import io.github.marcuscastelo.quartus.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe que implementa o Mod criado, inicializando-o juntamente com suas adições
 * Extende a classe ModInitializer, cuja função é inicializar os Mod's criados 
 */
public class Quartus implements ModInitializer {

	//Variáveis auxiliares para identificação do Mod
    public static final String MOD_ID = "quartus";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static Quartus INSTANCE;

	//Variável que separa um espaço no inventário, posiciona os itens/blocos criados
	//e "constrói" a interface para os itens criados, separando-os dos itens padrão
    public static ItemGroup ITEMGROUP =
            FabricItemGroupBuilder.create(id("item_group"))
                    .icon(()->new ItemStack(QuartusItems.FLOPPY_DISK))
                    .appendItems(QuartusItems::appendItemGroupStacksInorder)
                    .build();
	/**
	 * Método principal de inicialização do Mod, iniciando blocos, entidades de blocos, itens, etc
	 */
    @Override
    public void onInitialize() {
        System.out.println("onInitialize()");
        INSTANCE = this;

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
