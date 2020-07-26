package io.github.marcuscastelo.quartus.circuit;

import com.google.common.collect.ImmutableList;
import io.github.marcuscastelo.quartus.network.QuartusSimetricSerializer;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 * Classe que define um Bus no circuito.
 * Faz a conexão com os demais componentes do circuito,
 * transmitindo os bits de informações entre eles.
 */
public class QuartusBus {
    //Equivale a um bus de tamanho 1 e valor true
    public static final QuartusBus HIGH1b;
    //Equivale a um bus de tamanho 1 e valor false
    public static final QuartusBus LOW1b;

    //Método que copia o Bus, retornando-o
    public QuartusBus copy() {
        return new QuartusBus(this);
    }

    //Método que copia os valores de um Bus, retornando-os
    public QuartusBus(QuartusBus cloneFrom) {
        this.values = cloneFrom.values;
    }

    public ImmutableList<Boolean> values;

    /**
     * Método auxiliar para usado para converter os parâmetros de um construtor para outro
     * @param value         Primeiro valor do bus (boolean)
     * @param moreValues    Array com demais valores
     * @return              Lista com todos os valores
     */
    private static List<Boolean> convertParams(Boolean value, Boolean ...moreValues) {
        List<Boolean> list = new BooleanArrayList(moreValues.length+1);
        list.add(value);
        list.addAll(Arrays.asList(moreValues));
        return list;
    }

    /**
     *
     * @param value
     * @param moreValues
     */
    public QuartusBus(Boolean value, Boolean ...moreValues) {
        this(convertParams(value, moreValues));
    }

    public QuartusBus(List<Boolean> values) {
        if (values.size() == 0) throw new IllegalArgumentException("Trying to create empty QuartusBusInfo");
        this.values = ImmutableList.copyOf(values);
    }

    //Método que retorna o tamanho/capacidade de um Bus
    public int getBusSize() { return values.size(); }

    //Método que Setta os valores de um Bus por meio de uma lista de booleans
    public void setValue(QuartusBus copyFrom) {
        if (copyFrom.getBusSize() != this.getBusSize()) {
            throw new IllegalArgumentException("Different bus sizes, unable to setValue");
        }

        setValueIgnoreSize(copyFrom);
    }
    public void setValue(Boolean value, Boolean ...values) { setValue(new QuartusBus(value, values)); }

    private void setValueIgnoreSize(QuartusBus copyFrom) {
        values = copyFrom.values;
    }

    /**
     * Método auxiliar que compara se um Bus é igual a outro objeto
     * Se ambos forem do tipo Bus, compara seus conteúdos
     * Retorna true se forem o mesmo objeto com mesmo conteúdo
     * False se há qualquer diferença
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof QuartusBus)) return false;
        QuartusBus other = (QuartusBus) obj;
        if (other.values.size() != this.values.size()) return false;

        for (int i = 0; i < this.values.size(); i++) {
            if (this.values.get(i) != other.values.get(i)) return false;
        }

        return true;
    }

    /**
     * Método que faz o toString de um Bus, printando seus dados/bits
     */
    @Override
    public String toString() {
        StringBuilder valuesStr = new StringBuilder("{ ");
        for (boolean value: values) valuesStr.append(value?"1":"0" + " ");
        valuesStr.append("}");
        return "QuartusBusInfo{" +
                "values=" + valuesStr.toString() +
                '}';
    }

    /**
     * Operador bitwise que inverte todos os bits de um bus
     * @return Novo bus com os bits invertidos
     */
    public QuartusBus bitwiseNot() {
        return new QuartusBus(values.stream().map(b->!b).collect(Collectors.toList()));
    }

    /**
     * Operador bitwise que faz um "and" entre o bus atual e outro bus, bit a bit
     * @param otherBus segundo bus a ser comparado bit a bit
     * @return novo bus após a execução do procedimento
     */
    public QuartusBus bitwiseAnd(QuartusBus otherBus) {
        return binaryBitwiseOperation(otherBus, (a,b) -> a && b);
    }

    /**
     * Operador bitwise que faz um "or" entre o bus atual e outro bus, bit a bit
     * @param otherBus segundo bus a ser comparado bit a bit
     * @return novo bus após a execução do procedimento
     */
    public QuartusBus bitwiseOr(QuartusBus otherBus) {
        return binaryBitwiseOperation(otherBus, (a,b) -> a || b);
    }

    /**
     * Operador bitwise que faz um "xor" entre o bus atual e outro bus, bit a bit
     * @param otherBus segundo bus a ser comparado bit a bit
     * @return novo bus após a execução do procedimento
     */
    public QuartusBus bitwiseXor(QuartusBus otherBus) {
        return binaryBitwiseOperation(otherBus, (a,b) -> a ^ b);
    }

    /**
     * Operador bitwise que faz um "nand" entre o bus atual e outro bus, bit a bit
     * @param otherBus segundo bus a ser comparado bit a bit
     * @return novo bus após a execução do procedimento
     */
    public QuartusBus bitwiseNand(QuartusBus otherBus) {
        return binaryBitwiseOperation(otherBus, (a,b) -> !(a && b));
    }

    /**
     * Operador bitwise que faz um "nor" entre o bus atual e outro bus, bit a bit
     * @param otherBus segundo bus a ser comparado bit a bit
     * @return novo bus após a execução do procedimento
     */
    public QuartusBus bitwiseNor(QuartusBus otherBus) {
        return binaryBitwiseOperation(otherBus, (a,b) -> !(a || b));
    }

    /**
     * Operador bitwise que faz um "xnor" entre o bus atual e outro bus, bit a bit
     * @param otherBus segundo bus a ser comparado bit a bit
     * @return novo bus após a execução do procedimento
     */
    public QuartusBus bitwiseXnor(QuartusBus otherBus) {
        return binaryBitwiseOperation(otherBus, (a,b) -> a == b);
    }

    /**
     * Operador bitwise que faz uma certa operação genérica entre o bus atual e outro bus, bit a bit
     * @param otherBus segundo bus a ser comparado bit a bit
     * @param operationLogic operação a ser realizada, bit a bit
     * @return novo bus após a execução do procedimento
     */
    public QuartusBus binaryBitwiseOperation(QuartusBus otherBus, BiPredicate<Boolean, Boolean> operationLogic) {
        int newBusSize = Math.max(otherBus.getBusSize(), this.getBusSize());
        List<Boolean> newBusValues = new ArrayList<>(newBusSize);
        int i = 0;
        for (; i < this.getBusSize() && i < otherBus.getBusSize(); i++) {
            newBusValues.add(operationLogic.test(this.values.get(i), otherBus.values.get(i)));
        }

        for (; i < newBusSize; i++) newBusValues.add(false);

        return new QuartusBus(newBusValues);
    }

    static {
        HIGH1b = new QuartusBus(true);
        LOW1b = new QuartusBus(false);
    }

    /**
     * Classe que serializa o objeto para uma string ou faz o processo contrário
     */
    public static class Serializer implements QuartusSimetricSerializer<QuartusBus, String> {
        @Override
        public String serialize(QuartusBus bus) {
            StringBuilder builder = new StringBuilder();
            bus.values.stream().forEach(value -> builder.append(value?1:0).append(","));
            return builder.toString();
        }

        @Override
        public QuartusBus unserialize(String serial) {
            return new QuartusBus(Arrays.stream(serial.split(",")).map(s -> s.equals("1")).collect(Collectors.toList()));
        }
    }


}
