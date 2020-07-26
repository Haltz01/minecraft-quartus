package io.github.marcuscastelo.quartus.circuit;

import com.google.common.collect.ImmutableMap;
import io.github.marcuscastelo.quartus.circuit.components.ComponentDescriptor;
import io.github.marcuscastelo.quartus.circuit.components.InputDescriptor;
import io.github.marcuscastelo.quartus.circuit.components.OutputDescriptor;
import io.github.marcuscastelo.quartus.circuit.components.executor.ExecutableComponent;
import io.github.marcuscastelo.quartus.circuit.components.executor.ExecutableWorldInput;
import io.github.marcuscastelo.quartus.circuit.components.executor.ExecutableWorldOutput;
import io.github.marcuscastelo.quartus.circuit.components.info.ComponentExecutionInfo;
import io.github.marcuscastelo.quartus.network.QuartusSerializer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Consumer;

public class CircuitExecutor {
    private final CircuitDescriptor descriptor;

    ImmutableMap<Integer, ExecutableWorldInput> inputs;
    ImmutableMap<Integer, ExecutableComponent> otherComponents;
    ImmutableMap<Integer, ExecutableWorldOutput> outputs;

    private CircuitExecutor(World world, List<BlockPos> inputControllers, List<BlockPos> outputControllers, CircuitDescriptor descriptor) {
        this.descriptor = descriptor;

        Map<Integer, ExecutableWorldInput> inputs = new HashMap<>();
        Map<Integer, ExecutableWorldOutput> outputs = new HashMap<>();
        Map<Integer, ExecutableComponent> otherComponents = new HashMap<>();
        int inputCount = descriptor.getInputCount();
        int outputCount = descriptor.getOutputCount();
        if (inputControllers.size() < inputCount || outputControllers.size() < outputCount) {
            throw new IllegalArgumentException("Not enough IO");
        }
        List<InputDescriptor> inputDescriptions = descriptor.getInputsList();
        List<OutputDescriptor> outputDescriptions = descriptor.getOutputsList();

        for (int i = 0; i < inputCount; i++ ){
            InputDescriptor input = inputDescriptions.get(i);
            inputs.put(input.getID(), new ExecutableWorldInput(this, world, inputControllers.get(i), input));
        }
        for (int i = 0; i < outputCount; i++) {
            OutputDescriptor output = outputDescriptions.get(i);
            outputs.put(output.getID(), new ExecutableWorldOutput(this, world, outputControllers.get(i), output));
        }

        descriptor.getOtherComponentsList().forEach(componentDescriptor -> {
            otherComponents.put(componentDescriptor.getID(), new ExecutableComponent.Builder().setCircuitExecutor(this).setComponentDescriptor(componentDescriptor).build());
        });

        this.inputs = ImmutableMap.copyOf(inputs);
        this.outputs = ImmutableMap.copyOf(outputs);
        this.otherComponents = ImmutableMap.copyOf(otherComponents);

    }

    public ExecutableComponent getComponentByID(int ID) {
        ExecutableComponent component = otherComponents.getOrDefault(ID, null);
        if (component == null) component = inputs.getOrDefault(ID, null);
        if (component == null) component = outputs.getOrDefault(ID, null);
        if (component == null) throw new RuntimeException("Unknown component of ID " + ID);
        return component;
    }

    public void setComponentExecutionInfo(int ID, ComponentExecutionInfo executionInfo) {
        getComponentByID(ID).setExecutionInfo(executionInfo);
    }

    //Método que atualiza a saída dos Inputs
    //A saída do Input é a mesma que a sua entrada do mundo "real"
    private void updateInputs() {
        for (ExecutableWorldInput input: inputs.values()) {
            input.updateComponent();
        }
    }

    //Método que atualiza as saídas dos componentes
    //De acordo com cada entrada e tipo de componente
    //a saída calculada
    private void updateComponents() {
        for (ExecutableComponent component: otherComponents.values()) {
            component.updateComponent();
        }
    }

    //Método que atualiza a saída dos Outputs
    //A saída para o mundo "real" é a mesma que a entrada
    private void updateOutputs() {
        for (ExecutableWorldOutput output: outputs.values()) {
            output.updateComponent();
        }
    }

    //Método que faz a chamada de atualização para os componentes do circuito
    public void updateCircuit() {
        updateInputs();
        updateComponents();
        updateOutputs();
    }

    public static class Builder {
        World world;
        List<BlockPos> inputControllersPos;
        List<BlockPos> outputControllersPos;
        CircuitDescriptor circuitDescriptor;

        Consumer<CircuitExecutor> afterBuildCallback;

        public Builder() {
            inputControllersPos = Collections.emptyList();
            outputControllersPos = Collections.emptyList();
            afterBuildCallback = null;
        }

        public Builder setAfterBuildCallback(Consumer<CircuitExecutor> afterBuildCallback) {
            this.afterBuildCallback = afterBuildCallback;
            return this;
        }

        public Builder setWorld(World world) {
            this.world = world;
            return this;
        }

        public Builder setInputControllersPos(List<BlockPos> inputControllersPos) {
            this.inputControllersPos = inputControllersPos;
            return this;
        }

        public Builder setOutputControllersPos(List<BlockPos> outputControllersPos) {
            this.outputControllersPos = outputControllersPos;
            return this;
        }

        public Builder setCircuitDescriptor(CircuitDescriptor circuitDescriptor) {
            this.circuitDescriptor = circuitDescriptor;
            return this;
        }

        public CircuitExecutor build() {
            CircuitExecutor executor = new CircuitExecutor(world, inputControllersPos, outputControllersPos, circuitDescriptor);
            if (afterBuildCallback != null) afterBuildCallback.accept(executor);
            return executor;
        }
    }

    public static class Serializer implements QuartusSerializer<CircuitExecutor, CircuitExecutor.Builder, String> {
        private static ExecutableComponent.Serializer componentSerializer = new ExecutableComponent.Serializer();
        private static ComponentExecutionInfo.Serializer executionInfoSerializer = new ComponentExecutionInfo.Serializer();

        /*
            12=North:>1,1,1|1,0,1#South:0,0;13=
         */
        @Override
        public String serialize(CircuitExecutor circuitExecutor) {
            StringBuilder stringBuilder = new StringBuilder();
            for (ExecutableWorldInput input: circuitExecutor.inputs.values()) {
                stringBuilder.append(componentSerializer.serialize(input));
                stringBuilder.append(";");
            }
            for (ExecutableComponent component: circuitExecutor.otherComponents.values()) {
                stringBuilder.append(componentSerializer.serialize(component));
                stringBuilder.append(";");
            }
            for (ExecutableWorldOutput output: circuitExecutor.outputs.values()) {
                stringBuilder.append(componentSerializer.serialize(output));
                stringBuilder.append(";");
            }
            return stringBuilder.toString();
        }

        @Override
        public CircuitExecutor.Builder unserialize(String serial) {
            String[] componentsSerial = serial.split(";");
            List<Consumer<CircuitExecutor>> afterCircuitExecutorBuildRunnables = new ArrayList<>(componentsSerial.length);
            for (String componentSerial: componentsSerial) {
                String[] parts = componentSerial.split("=");
                int ID = Integer.parseInt(parts[0]);
                ComponentExecutionInfo executionInfo = executionInfoSerializer.unserialize(parts[1]);
                afterCircuitExecutorBuildRunnables.add(executor -> executor.setComponentExecutionInfo(ID, executionInfo));
            }

            Builder executorBuilder = new Builder();
            executorBuilder.setAfterBuildCallback((executor -> afterCircuitExecutorBuildRunnables.forEach(consumer -> consumer.accept(executor))));
            return executorBuilder;
        }
    }

    public String serialize() {
        return new Serializer().serialize(this);
    }
}
