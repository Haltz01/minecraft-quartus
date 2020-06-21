package io.github.marcuscastelo.quartus.registry;

import io.github.marcuscastelo.quartus.circuit_logic.QuartusWorldNode;

import java.util.HashMap;

public class QuartusNodes {
    private static HashMap<String, Class<QuartusWorldNode>> classHashMap = new HashMap<>();
    public static void register(String nodeType, Class<QuartusWorldNode> subClass) {
        classHashMap.putIfAbsent(nodeType, subClass);
    }
}
