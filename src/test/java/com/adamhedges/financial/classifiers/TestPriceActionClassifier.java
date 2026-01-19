package com.adamhedges.financial.classifiers;

import com.adamhedges.financial.core.bars.PriceBar;
import com.adamhedges.financial.storage.buffers.RandomAccessBuffer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestPriceActionClassifier {

    private PriceBar getBar(double open, double high, double low, double close) {
        PriceBar bar = new PriceBar("TEST");
        bar.setOpen(open);
        bar.setHigh(high);
        bar.setLow(low);
        bar.setClose(close);
        return bar;
    }

    @Test
    public void TestPriceActionClassifier_isHammer() {
        RandomAccessBuffer<PriceBar> buffer = new RandomAccessBuffer<>(1);
        PriceActionClassifier classifier = new PriceActionClassifier(buffer);

        // empty state
        Assertions.assertFalse(classifier.isHammer());

        // down bar
        buffer.add(getBar(10, 10, 9, 9));
        Assertions.assertFalse(classifier.isHammer());

        // up bar, but not hammer
        buffer.add(getBar(10, 11, 9, 11));
        Assertions.assertFalse(classifier.isHammer());

        // hammer
        buffer.add(getBar(10, 11.06, 6.5, 11));
        Assertions.assertTrue(classifier.isHammer());
    }

    @Test
    public void TestPriceActionClassifier_isBullishEngulfing() {
        RandomAccessBuffer<PriceBar> buffer = new RandomAccessBuffer<>(2);
        PriceActionClassifier classifier = new PriceActionClassifier(buffer);

        // empty state
        Assertions.assertFalse(classifier.isBullishEngulfing());

        // single bar
        buffer.add(getBar(10, 10, 9, 9));
        Assertions.assertFalse(classifier.isBullishEngulfing());

        // second bar, also down
        buffer.add(getBar(10.5, 10.5, 9.5, 9.5));
        Assertions.assertFalse(classifier.isBullishEngulfing());

        // third bar, up engulfing
        buffer.add(getBar(9.5, 11, 9.5, 11));
        Assertions.assertTrue(classifier.isBullishEngulfing());
    }

}
