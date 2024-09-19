package com.adamhedges.daytrading.library.functions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class RelativeMovingAverage extends WindowFunction {

    // from TradingView's definition (https://www.tradingcode.net/tradingview/relative-moving-average///ref-1)
    // RMA = alpha * source + (1 - alpha) * RMA[1]
    // alpha = 1 / period
    // initial RMA is SMA

    private final BigDecimal alpha;
    private BigDecimal average = null;

    public RelativeMovingAverage(int period, List<BigDecimal> prices) {
        super(period, prices);
        alpha = BigDecimal.ONE.divide(BigDecimal.valueOf(buffer.getPeriod()), RoundingMode.HALF_UP);
        setIndex(buffer.getIndex());
    }

    @Override
    public void setIndex(int newIndex) {
        buffer.setIndex(newIndex);
        average = getBufferSum().divide(BigDecimal.valueOf(buffer.getPeriod()), RoundingMode.HALF_UP);
    }

    @Override
    public void slide(BigDecimal value) {
        if (average == null) {
            setIndex(buffer.getIndex());
            return;
        }

        average = alpha.multiply(value).add((BigDecimal.ONE.subtract(alpha).multiply(average)));
        if (buffer.isRingBuffer()) {
            values.set(buffer.getLowerBound(), value);
        }

        buffer.advance();
    }

    @Override
    public void calculate() {
        // calculation is handled directly in setIndex and slide
    }

    @Override
    public BigDecimal getValue() {
        return average;
    }

}
