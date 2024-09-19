package com.adamhedges.daytrading.library.functions;

import java.math.BigDecimal;
import java.util.List;

public abstract class WindowFunction {

    protected final List<BigDecimal> values;
    protected final BufferContainer buffer;

    public WindowFunction(int period, List<BigDecimal> prices) {
        values = prices;
        buffer = new BufferContainer(period - 1, period, values.size());
    }

    public BigDecimal getFirstValue() {
        return values.get(buffer.getLowerBound());
    }

    public BigDecimal getLastValue() {
        return values.get(buffer.getUpperBound(0));
    }

    public int getBufferIndex() {
        return buffer.getIndex();
    }

    public void setBufferIndex(int newIndex) {
        buffer.setIndex(newIndex);
    }

    public BigDecimal getBufferSum() {
        return values.subList(buffer.getLowerBound(), buffer.getUpperBound(1))
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getBufferSumSq() {
        return values.subList(buffer.getLowerBound(), buffer.getUpperBound(1))
                .stream()
                .reduce(BigDecimal.ZERO, (a,b) -> a.add(b.pow(2)));
    }

    public abstract void setIndex(int newIndex);
    public abstract void slide(BigDecimal newValue);
    public abstract void calculate();
    public abstract BigDecimal getValue();

}
