package com.adamhedges.financial.functions;

import com.adamhedges.financial.core.bars.PriceBar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

public class TestStandardDeviation {

    private void checkStats(StandardDeviation func, int newindex) {
        func.slide(BigDecimal.valueOf(Context.data[newindex]));
        int testindex = (func.buffer.getIndex() - func.buffer.getPeriod() + 1) % Context.dev.length;
        Assertions.assertEquals(Context.dev[testindex], func.getValue().round(MathContext.DECIMAL64).doubleValue(), 0.0001);
    }

    @Test
    public void TestStandardDeviation_linearBuffer() {
        List<PriceBar> prices = Context.getPriceBarList(Context.data.length);
        StandardDeviation stdev = new StandardDeviation(Context.period, Context.mapClosePricesFromBars(prices));
        while (stdev.buffer.getIndex() < Context.data.length + 10) {
            int newindex = (stdev.buffer.getIndex() + 1) % stdev.buffer.getLength();
            checkStats(stdev, newindex);
        }
    }

    @Test
    public void TestStandardDeviation_ringBuffer() {
        List<PriceBar> prices = Context.getPriceBarList(Context.period);
        StandardDeviation stdev = new StandardDeviation(Context.period, Context.mapClosePricesFromBars(prices));
        while (stdev.buffer.getIndex() < Context.data.length + 10) {
            int newindex = (stdev.buffer.getIndex() + 1) % Context.data.length;
            checkStats(stdev, newindex);
        }
    }

}
