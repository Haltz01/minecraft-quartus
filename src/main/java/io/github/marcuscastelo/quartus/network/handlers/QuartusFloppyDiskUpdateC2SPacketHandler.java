package io.github.marcuscastelo.quartus.network.handlers;

import io.github.marcuscastelo.quartus.blockentity.ImplementedInventory;
import io.github.marcuscastelo.quartus.circuit.CircuitDescriptor;
import io.github.marcuscastelo.quartus.circuit.components.ComponentDescriptor;
import io.github.marcuscastelo.quartus.network.QuartusFloppyDiskUpdateC2SPacket;
import io.github.marcuscastelo.quartus.registry.QuartusCircuitComponents;
import io.github.marcuscastelo.quartus.registry.QuartusItems;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Classe que recebe e gerencia pacote de bytes do servidor
 * Atualiza os FLOPPY_DISK para todos os jogadores no servidor
 * quando compilado no bloco Compiler
 */
public class QuartusFloppyDiskUpdateC2SPacketHandler {
    public static void register() {
        ServerSidePacketRegistry.INSTANCE.register(QuartusFloppyDiskUpdateC2SPacket.ID, (packetContext, packetByteBuf) -> {
            final QuartusFloppyDiskUpdateC2SPacket packet = new QuartusFloppyDiskUpdateC2SPacket();
            packet.read(packetByteBuf);

            World world = packetContext.getPlayer().world;

            packetContext.getTaskQueue().execute(() -> {
                Inventory inv =  (ImplementedInventory) world.getBlockEntity(packet.getCompilerPos());
                if (inv == null) {
                    System.err.println("ERROR: invalid compiler inventory while updating floppy disk info");
                    return;
                }

                ItemStack itemStack = inv.getInvStack(0);
                if (itemStack.isEmpty()) return;
                if (!itemStack.getItem().equals(QuartusItems.FLOPPY_DISK)) return;


                String circuitStr = packet.getCompoundTag().getString("circuit");
                for (int i = 0; i < 10; i++) {
                    CircuitDescriptor descriptor = new CircuitDescriptor.Serializer().unserialize(circuitStr);
                    circuitStr = descriptor.serialize();
                }

                packet.getCompoundTag().putString("circuit", circuitStr);

                itemStack.setTag(packet.getCompoundTag());
            });
        });
    }
}
