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
        Assertions.assertFalse(classifier.isHammer());

        buffer.add(getBar(10, 10, 9, 9)); // down bar
        Assertions.assertFalse(classifier.isHammer());

        buffer.add(getBar(10, 11, 9, 11)); // up bar but not hammer
        Assertions.assertFalse(classifier.isHammer());

        buffer.add(getBar(10.5, 11.06, 6.5, 11)); // hammer
        Assertions.assertTrue(classifier.isHammer());
    }

}
