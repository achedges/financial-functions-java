package com.adamhedges.daytrading.library.functions;

import com.adamhedges.daytrading.library.bars.PriceBar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

public class TestSimpleMovingAverage {

    private void checkStats(SimpleMovingAverage func, int newindex) {
        func.slide(BigDecimal.valueOf(Context.data[newindex]));
        int testindex = (func.buffer.getIndex() - func.buffer.getPeriod() + 1) % Context.sma.length;
        Assertions.assertEquals(Context.sma[testindex], func.getValue().round(MathContext.DECIMAL64).doubleValue());
    }

    @Test
    public void TestSimpleMovingAverage_linearBuffer() {
        List<PriceBar> prices = Context.getPriceBarList(Context.data.length);
        SimpleMovingAverage sma = new SimpleMovingAverage(Context.period, Context.mapClosePricesFromBars(prices));
        while (sma.buffer.getIndex() < Context.data.length + 10) {
            int newindex = (sma.buffer.getIndex() + 1) % sma.buffer.getLength();
            checkStats(sma, newindex);
        }
    }

    @Test
    public void TestSimpleMovingAverage_ringBuffer() {
        List<PriceBar> prices = Context.getPriceBarList(Context.period);
        SimpleMovingAverage sma = new SimpleMovingAverage(Context.period, Context.mapClosePricesFromBars(prices));
        while (sma.buffer.getIndex() < Context.data.length + 10) {
            int newindex = (sma.buffer.getIndex() + 1) % Context.data.length;
            checkStats(sma, newindex);
        }
    }

}
