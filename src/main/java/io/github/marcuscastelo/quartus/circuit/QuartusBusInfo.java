package io.github.marcuscastelo.quartus.circuit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classe que define um Bus no circuito.
 * Faz a conexão com os demais componentes do circuito,
 * transmitindo os bits de informações entre eles.
 */
public class QuartusBusInfo {
	//Variáveis que definem o true(1) e o false(0) no Bus
    public static final QuartusBusInfo HIGH1b;
    public static final QuartusBusInfo LOW1b;

	//Método que copia o Bus, retornando-o
    public QuartusBusInfo copy() {
        return new QuartusBusInfo(this);
    }

	//Método que copia os valores de um Bus, retornando-os
    public QuartusBusInfo(QuartusBusInfo cloneFrom) {
        this.values = new ArrayList<>(cloneFrom.values);
    }

	//Método que Setta os valores de um Bus por meio de uma lista de booleans
    public List<Boolean> values;
    public QuartusBusInfo(Boolean ...values) {
        if (values.length == 0) throw new IllegalArgumentException("Trying to create empty QuartusBusInfo");
        this.values = Arrays.asList(values);
    }

	//Método que retorna o tamanho/capacidade de um Bus
    public int getBusSize() { return values.size(); }

	//Método que Setta os valores de um Bus, copiando de outro
	public void setValue(QuartusBusInfo copyFrom) { values = new ArrayList<>(copyFrom.values); }
	
	//Método que Setta os valores de um Bus, de acordo com o vetor de entrada
    public void setValue(Boolean ...values) { setValue(new QuartusBusInfo(values)); }


	/**
	 * Método auxiliar que compara se um Bus é igual a outro objeto
	 * Se ambos forem do tipo Bus, compara seus conteúdos
	 * Retorna true se forem o mesmo objeto com mesmo conteúdo
	 * False se há qualquer diferença
	 */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof QuartusBusInfo)) return false;
        QuartusBusInfo other = (QuartusBusInfo) obj;
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

	//Define o que significa true e false num Bus
    static {
        HIGH1b = new QuartusBusInfo(true);
        LOW1b = new QuartusBusInfo(false);
    }
}
