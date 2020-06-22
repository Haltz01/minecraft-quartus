package io.github.marcuscastelo.quartus.circuit;

import java.util.Arrays;
import java.util.List;

public class QuartusBusInfo {
    public static final QuartusBusInfo HIGH1b;
    public static final QuartusBusInfo LOW1b;

    List<Boolean> values;
    public QuartusBusInfo(Boolean ...values) {
        this.values = Arrays.asList(values);
    }

    public int getBusSize() { return values.size(); }

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

    static {
        HIGH1b = new QuartusBusInfo(true);
        LOW1b = new QuartusBusInfo(false);
    }
}
