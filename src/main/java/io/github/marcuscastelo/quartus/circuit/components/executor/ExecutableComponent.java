package io.github.marcuscastelo.quartus.circuit.components.executor;

import com.google.common.collect.ImmutableList;
import io.github.marcuscastelo.quartus.circuit.CircuitExecutor;
import io.github.marcuscastelo.quartus.circuit.ComponentConnection;
import io.github.marcuscastelo.quartus.circuit.QuartusBus;
import io.github.marcuscastelo.quartus.circuit.components.ComponentDescriptor;
import io.github.marcuscastelo.quartus.circuit.components.info.ComponentExecutionInfo;
import io.github.marcuscastelo.quartus.network.QuartusBuildable;
import io.github.marcuscastelo.quartus.network.QuartusSerializer;
import io.github.marcuscastelo.quartus.network.QuartusSimetricSerializer;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class ExecutableComponent extends QuartusBuildable<ExecutableComponent> {
    protected CircuitExecutor executor;
    protected ComponentDescriptor descriptor;
    protected ComponentExecutionInfo executionInfo;


    //TODO: make private
    protected ExecutableComponent(CircuitExecutor executor, ComponentDescriptor componentDescriptor, ComponentExecutionInfo executionInfo) {
        this.executor = executor;
        this.descriptor = componentDescriptor;
        this.executionInfo = executionInfo;
    }

    public void setExecutionInfo(ComponentExecutionInfo executionInfo) {
        this.executionInfo = executionInfo;
    }

    /**
     * Método que retorna as informações que estão nos inputs e outputs de um componente
     * @return		Mapeamento dos Bus's com suas respectivas informações
     */
    public ComponentExecutionInfo getExecutionInfo() {
        return executionInfo;
    }

    //TODO: tornar mais genérica: atualmente foca apenas em trazer a saída do outro (supondo ser única) para a entrada deste (supondo ser única)
    private void updateInputValues() {
        forDirection:
        for (Map.Entry<Direction, List<ComponentConnection>> entry: descriptor.getConnections().entrySet()) {
            Direction AtoBDirection = entry.getKey();

            //FIXME: suporta apenas uma entrada e uma saída
            List<ComponentConnection> possibleConnections = entry.getValue();
            if (entry.getValue().size() == 0) continue; //nenhuma conexão nessa direção
            ComponentConnection arbitrarilyChosenConnection = null;

            //Pega a primeira conexão de input encontrada (ou desiste da direção se nenhuma for encontrada)
            for (int i = 0; i < possibleConnections.size(); i++) {
                arbitrarilyChosenConnection = entry.getValue().get(i);
                if (arbitrarilyChosenConnection.getType() == ComponentConnection.ConnectionType.INPUT) break;
                if (i == possibleConnections.size()-1) continue forDirection;
            }

            int BID = ComponentDescriptor.getComponentStrInfo(arbitrarilyChosenConnection.connectToCompStr).getRight();
            ExecutableComponent BComponent = executor.getComponentByID(BID);

            Direction BtoADirection = arbitrarilyChosenConnection.BtoADirection;

            //Copia o output do B para o input do atual (A)
            ImmutableList<QuartusBus> BOutputs = BComponent.executionInfo.getOutput(BtoADirection);
            this.executionInfo.setInput(AtoBDirection, BOutputs);
        }
    }

    /**
     * Método que faz a chamada do updateInputInfo de um circuito,
     * atualizando seus valores de entrada e saída
     */
    public void updateComponent() {
        updateInputValues();
        if (descriptor.getLogic() != null) descriptor.getLogic().execute(executionInfo);
    }

    public ComponentDescriptor getDescriptor() {
        return descriptor;
    }


    public static class Builder extends QuartusBuildable.Builder<ExecutableComponent> {
        CircuitExecutor circuitExecutor;
        ComponentDescriptor componentDescriptor;
        ComponentExecutionInfo executionInfo;
        public Builder() {}

        public Builder setCircuitExecutor(CircuitExecutor circuitExecutor) {
            this.circuitExecutor = circuitExecutor;
            return this;
        }

        public Builder setComponentDescriptor(ComponentDescriptor componentDescriptor) {
            this.componentDescriptor = componentDescriptor;
            return this;
        }

        public Builder setExecutionInfo(ComponentExecutionInfo executionInfo) {
            this.executionInfo = executionInfo;
            return this;
        }

        public ExecutableComponent build() {
            if (circuitExecutor == null) throw new NullPointerException("CircuitExecutor must be informed");
            if (componentDescriptor == null) throw new NullPointerException("ComponentDescriptor must be informed");

            if (executionInfo == null) {
                executionInfo = new ComponentExecutionInfo(componentDescriptor.getComponentDirectionInfo());
            }
            return new ExecutableComponent(circuitExecutor, componentDescriptor, executionInfo);
        }
    }


    public static class Serializer implements QuartusSerializer<ExecutableComponent, ExecutableComponent.Builder, String> {
        @Override
        public String serialize(ExecutableComponent component) throws SerializationException {
            return component.descriptor.getID() +
                    "=" +
                    new ComponentExecutionInfo.Serializer().serialize(component.getExecutionInfo());
        }

        @Override
        public ExecutableComponent.Builder unserialize(String serial) throws SerializationException {
            if (true) throw new RuntimeException("nao to entendendo!?!?");
            Builder compBuilder = new ExecutableComponent.Builder();
            String[] parts = serial.split("=");
            if (parts.length != 2) throw new SerializationException();
            String compIDStr = parts[0];
            if (!StringUtils.isNumeric(compIDStr)) throw new SerializationException();
            int compID = Integer.parseInt(compIDStr);
            String executionInfoSerial = parts[1];
            ComponentExecutionInfo executionInfo = new ComponentExecutionInfo.Serializer().unserialize(executionInfoSerial);
            compBuilder.setExecutionInfo(executionInfo);
            return compBuilder;
        }
    }


}
