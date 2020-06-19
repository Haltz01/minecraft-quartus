package io.github.marcuscastelo.quartus.registry;

import io.github.marcuscastelo.quartus.circuit_logic.QuartusNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class QuartusNodes {
    private static HashMap<String, Class<QuartusNode>> classHashMap = new HashMap<>();
    public static void register(String nodeType, Class<QuartusNode> subClass) {
        classHashMap.putIfAbsent(nodeType, subClass);
    }
}
