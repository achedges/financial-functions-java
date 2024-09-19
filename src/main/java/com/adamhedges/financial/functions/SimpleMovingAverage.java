package com.adamhedges.financial.functions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class SimpleMovingAverage extends WindowFunction {

    private BigDecimal sumValues = null;
    private BigDecimal average = null;

    public SimpleMovingAverage(int period, List<BigDecimal> prices) {
        super(period, prices);
        setIndex(buffer.getIndex());
    }

    @Override
    public void setIndex(int newIndex) {
        buffer.setIndex(newIndex);
        sumValues = getBufferSum();
        calculate();
    }

    @Override
    public void slide(BigDecimal newValue) {
        int oldIndex = buffer.getLowerBound();
        if (sumValues == null) {
            setIndex(buffer.getIndex());
        }

        sumValues = sumValues.add(newValue.subtract(values.get(oldIndex)));
        calculate();

        if (buffer.isRingBuffer()) {
            values.set(oldIndex, newValue);
        }

        buffer.advance();
    }

    @Override
    public void calculate() {
        average = sumValues.divide(BigDecimal.valueOf(buffer.getPeriod()), 6, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getValue() {
        return average;
    }

}
