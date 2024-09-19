package com.adamhedges.daytrading.library.functions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ExponentialMovingAverage extends WindowFunction {

    private BigDecimal ema = null;
    private BigDecimal price;
    private final BigDecimal weight;

    public ExponentialMovingAverage(int period, List<BigDecimal> prices) {
        super(period, prices);
        price = BigDecimal.ZERO;
        weight = BigDecimal.valueOf(2.0).divide(BigDecimal.valueOf(period + 1), 6, RoundingMode.HALF_UP);
        setIndex(buffer.getIndex());
    }

    @Override
    public void setIndex(int newIndex) {
        buffer.setIndex(newIndex);
        ema = null;
        calculate();
    }

    @Override
    public void slide(BigDecimal newValue) {
        price = newValue;
        if (buffer.isRingBuffer()) {
            values.set(buffer.getLowerBound(), price);
        }
        calculate();
        buffer.advance();
    }

    @Override
    public void calculate() {
        if (ema == null) {
            BigDecimal _sum = getBufferSum();
            ema = _sum.divide(BigDecimal.valueOf(buffer.getPeriod()), 6, RoundingMode.HALF_UP);
        } else {
            ema = price.subtract(ema).multiply(weight).add(ema);
        }
    }

    @Override
    public BigDecimal getValue() {
        return ema;
    }

}
