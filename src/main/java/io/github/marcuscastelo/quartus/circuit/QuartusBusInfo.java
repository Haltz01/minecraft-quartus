package io.github.marcuscastelo.quartus.circuit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuartusBusInfo {
    public static final QuartusBusInfo HIGH1b;
    public static final QuartusBusInfo LOW1b;

    public QuartusBusInfo copy() {
        return new QuartusBusInfo(this);
    }

    public QuartusBusInfo(QuartusBusInfo cloneFrom) {
        this.values = new ArrayList<>(cloneFrom.values);
    }

    public List<Boolean> values;
    public QuartusBusInfo(Boolean ...values) {
        if (values.length == 0) throw new IllegalArgumentException("Trying to create empty QuartusBusInfo");
        this.values = Arrays.asList(values);
    }

    public int getBusSize() { return values.size(); }

    public void setValue(QuartusBusInfo copyFrom) { values = new ArrayList<>(copyFrom.values); }
    public void setValue(Boolean ...values) { setValue(new QuartusBusInfo(values)); }


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

    @Override
    public String toString() {
        StringBuilder valuesStr = new StringBuilder("{ ");
        for (boolean value: values) valuesStr.append(value?"1":"0" + " ");
        valuesStr.append("}");
        return "QuartusBusInfo{" +
                "values=" + valuesStr.toString() +
                '}';
    }

    static {
        HIGH1b = new QuartusBusInfo(true);
        LOW1b = new QuartusBusInfo(false);
    }
}
