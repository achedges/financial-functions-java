package com.adamhedges.financial.functions;

import com.adamhedges.financial.core.bars.PriceBar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

public class TestExponentialMovingAverage {

    // we can't let the rolling calculation wrap all the way around, since the initial EMA calculation is an SMA

    private void checkStats(ExponentialMovingAverage func, int newindex) {
        func.slide(BigDecimal.valueOf(Context.data[newindex]));
        int testindex = (func.buffer.getIndex() - func.buffer.getPeriod() + 1) % Context.ema.length;
        Assertions.assertEquals(Context.ema[testindex], func.getValue().round(MathContext.DECIMAL64).doubleValue(), 0.0001);
    }

    @Test
    public void TestExponentialMovingAverage_linearBuffer() {
        List<PriceBar> prices = Context.getPriceBarList(Context.data.length);
        ExponentialMovingAverage ema = new ExponentialMovingAverage(Context.period, Context.mapClosePricesFromBars(prices));
        while (ema.buffer.getIndex() < Context.data.length + 2) {
            int newindex = (ema.buffer.getIndex() + 1) % ema.buffer.getLength();
            checkStats(ema, newindex);
        }
    }

    @Test
    public void TestExponentialMovingAverage_ringBuffer() {
        List<PriceBar> prices = Context.getPriceBarList(Context.period);
        ExponentialMovingAverage ema = new ExponentialMovingAverage(Context.period, Context.mapClosePricesFromBars(prices));
        while (ema.buffer.getIndex() < Context.data.length + 2) {
            int newindex = (ema.buffer.getIndex() + 1) % Context.data.length;
            checkStats(ema, newindex);
        }
    }

}
