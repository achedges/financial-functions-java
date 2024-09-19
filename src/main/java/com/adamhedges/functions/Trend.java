package com.adamhedges.daytrading.library.functions;

import com.adamhedges.daytrading.library.util.Comparisons;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class Trend extends WindowFunction {

    private BigDecimal trend = null;
    private BigDecimal oldValue = null;
    private BigDecimal newValue = null;

    public Trend(int period, List<BigDecimal> prices) {
        super(period, prices);
        setIndex(buffer.getIndex());
    }

    @Override
    public void setIndex(int newIndex) {
        buffer.setIndex(newIndex);
        oldValue = values.get(buffer.getLowerBound());
        newValue = getLastValue();
        calculate();
    }

    @Override
    public void slide(BigDecimal value) {
        if (Comparisons.eq(oldValue, BigDecimal.ZERO) && Comparisons.eq(newValue, BigDecimal.ZERO)) {
            setIndex(buffer.getIndex());
        }

        if (buffer.isRingBuffer()) {
            values.set(buffer.getLowerBound(), value);
        }

        buffer.advance();
        oldValue = values.get(buffer.getLowerBound());
        newValue = getLastValue();
        calculate();
    }

    @Override
    public void calculate() {
        trend = newValue.subtract(oldValue).divide(oldValue, 6, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getValue() {
        return trend;
    }

}
