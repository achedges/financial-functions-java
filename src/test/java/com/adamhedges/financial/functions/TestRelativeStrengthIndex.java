package com.adamhedges.financial.functions;

import com.adamhedges.financial.core.bars.PriceBar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestRelativeStrengthIndex {

    @Test
    public void TestRelativeStrengthIndex_slide() {
        List<PriceBar> bars = List.of(
            new PriceBar("", 54.80),
            new PriceBar("", 56.80),
            new PriceBar("", 57.85),
            new PriceBar("", 59.85),
            new PriceBar("", 60.57),
            new PriceBar("", 61.10),
            new PriceBar("", 62.17),
            new PriceBar("", 60.60),
            new PriceBar("", 62.35),
            new PriceBar("", 62.15),
            new PriceBar("", 62.35),
            new PriceBar("", 61.45),
            new PriceBar("", 62.80),
            new PriceBar("", 61.37)
        );

        RelativeStrengthIndex rsi = new RelativeStrengthIndex(bars.size(), bars);

        rsi.slide(new PriceBar("", 62.50));
        Assertions.assertEquals(2.39, rsi.getRelativeStrength(), 0.001);
        Assertions.assertEquals(70.50, rsi.getRSI(), 0.01);
    }

}
