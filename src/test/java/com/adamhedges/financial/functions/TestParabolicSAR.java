package com.adamhedges.financial.functions;

import com.adamhedges.financial.core.bars.PriceBar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestParabolicSAR {

    private PriceBar getBar(double open, double close) {
        PriceBar bar = new PriceBar("", open);
        bar.setClose(close); // set this first so isUp() works
        bar.setHigh(bar.isUp() ? close : open);
        bar.setLow(bar.isUp() ? open : close);
        return bar;
    }

    @Test
    public void TestParabolicSAR_init() {
        PriceBar bar = new PriceBar("", 10.0);
        bar.setHigh(11.0);
        bar.setClose(11.0);

        ParabolicSAR parabolicSAR = new ParabolicSAR(bar);
        Assertions.assertTrue(parabolicSAR.isLong());
        Assertions.assertEquals(10.0, parabolicSAR.getStop());
        Assertions.assertEquals(10.0, parabolicSAR.getLow());
        Assertions.assertEquals(11.0, parabolicSAR.getHigh());
    }

    @Test
    public void TestParabolicSAR_slide_basic_long() {
        double[][] prices = {
            {51.5, 52.5},
            {52, 53},
            {52.5, 53.5},
            {53, 54},
            {53.5, 54.5},
            {54, 55},
            {54.5, 55.5},
            {55, 56},
            {55.5, 56.5},
            {56, 57},
            {56.5, 57.5},
            {57, 58},
            {57.5, 58.5},
            {58, 59}
        };

        double[] expectedSar = {50.0, 50.06, 50.19, 50.43, 50.75, 51.17, 51.69, 52.29, 52.97, 53.69, 54.45, 55.16, 55.83, 56.46, 56.75};

        ParabolicSAR psar = new ParabolicSAR(getBar(50, 51));
        Assertions.assertEquals(50.0, psar.getStop());

        for (int i = 0; i < prices.length; i++) {
            psar.slide(getBar(prices[i][0], prices[i][1]));
            Assertions.assertEquals(expectedSar[i], psar.getStop(), 0.01);
        }

        Assertions.assertEquals(0.2, psar.getAlpha());
    }

    @Test
    public void TestParabolicSAR_slide_basic_short() {
        double[][] prices = {
            {59, 58},
            {58.5, 57.5},
            {58, 57},
            {57.5, 56.5},
            {57, 56},
            {56.5, 55.5},
            {56, 55},
            {55.5, 54.5},
            {55, 54},
            {54.5, 53.5},
            {54, 53},
            {53.5, 52.5},
            {53, 52},
            {52.5, 51.5}
        };

        double[] expectedSar = {60.5, 60.44, 60.31, 60.07, 59.75, 59.33, 58.81, 58.21, 57.53, 56.81, 56.05, 55.34, 54.67, 54.04, 53.75};
        ParabolicSAR psar = new ParabolicSAR(getBar(60.5, 59.5));
        Assertions.assertEquals(60.5, psar.getStop());

        for (int i = 0; i < prices.length; i++) {
            psar.slide(getBar(prices[i][0], prices[i][1]));
            Assertions.assertEquals(expectedSar[i], psar.getStop(), 0.01);
        }
    }

    @Test
    public void TestParabolicSAR_slide_reversal() {
        double[][] prices = {
            {51.5, 52.5},
            {52, 53},
            {52.5, 53.5}, // max high should become new stop
            {53, 49.99},
            {50.5, 49.5},
            {50, 49}
        };

        double[] expectedSar = {50.0, 50.06, 50.19, 53.5, 53.5, 53.41};

        ParabolicSAR psar = new ParabolicSAR(getBar(50, 51));
        Assertions.assertEquals(50.0, psar.getStop());
        Assertions.assertTrue(psar.isLong()); // should start long

        for (int i = 0; i < prices.length; i++) {
            psar.slide(getBar(prices[i][0], prices[i][1]));
            Assertions.assertEquals(expectedSar[i], psar.getStop(), 0.01);

            if (i == 3) {
                Assertions.assertTrue(psar.isReversalSignal());
            } else {
                Assertions.assertFalse(psar.isReversalSignal());
            }
        }

        Assertions.assertFalse(psar.isLong()); // should end short
    }

}
