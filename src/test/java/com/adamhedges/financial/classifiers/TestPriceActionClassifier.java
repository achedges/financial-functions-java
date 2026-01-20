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

    @Test
    public void TestPriceActionClassifier_isPiercing() {
        RandomAccessBuffer<PriceBar> buffer = new RandomAccessBuffer<>(2);
        PriceActionClassifier classifier = new PriceActionClassifier(buffer);

        // empty state
        Assertions.assertFalse(classifier.isPiercing());

        // single bar
        buffer.add(getBar(10, 10, 9, 9));
        Assertions.assertFalse(classifier.isPiercing());

        // second bar, also down
        buffer.add(getBar(10.5, 10.5, 9.5, 9.5));
        Assertions.assertFalse(classifier.isPiercing());

        // third bar, up engulfing
        buffer.add(getBar(9.5, 11, 9.5, 10.1));
        Assertions.assertTrue(classifier.isPiercing());
    }

    @Test
    public void TestPriceActionClassifier_isTweezerBottom() {
        RandomAccessBuffer<PriceBar> buffer = new RandomAccessBuffer<>(2);
        PriceActionClassifier classifier = new PriceActionClassifier(buffer);

        // empty state
        Assertions.assertFalse(classifier.isTweezerBottom());

        // single bar
        buffer.add(getBar(10, 10, 9, 9));
        Assertions.assertFalse(classifier.isTweezerBottom());

        // second bar, also down
        buffer.add(getBar(10, 10, 7.5, 9));
        Assertions.assertFalse(classifier.isTweezerBottom());

        // third bar, up tweezer
        buffer.add(getBar(9, 10, 7.5, 10));
        Assertions.assertTrue(classifier.isTweezerBottom());
    }

    @Test
    public void TestPriceActionClassifier_isMorningStar() {
        RandomAccessBuffer<PriceBar> buffer = new RandomAccessBuffer<>(3);
        PriceActionClassifier classifier = new PriceActionClassifier(buffer);

        // empty state
        Assertions.assertFalse(classifier.isMorningStar());

        // single bar
        buffer.add(getBar(10, 10, 9, 9));
        Assertions.assertFalse(classifier.isMorningStar());

        // second bar
        buffer.add(getBar(9, 9.04, 8.96, 9));
        Assertions.assertFalse(classifier.isMorningStar());

        // third bar, morning star
        buffer.add(getBar(9, 10, 9, 10));
        Assertions.assertTrue(classifier.isMorningStar());
    }

    @Test
    public void TestPriceActionClassifier_isShootingStar() {
        RandomAccessBuffer<PriceBar> buffer = new RandomAccessBuffer<>(1);
        PriceActionClassifier classifier = new PriceActionClassifier(buffer);

        // empty state
        Assertions.assertFalse(classifier.isShootingStar());

        // up bar
        buffer.add(getBar(9, 10, 9, 10));
        Assertions.assertFalse(classifier.isShootingStar());

        // down bar, but not shooting star
        buffer.add(getBar(11, 11, 9, 10));
        Assertions.assertFalse(classifier.isShootingStar());

        // shooting star
        buffer.add(getBar(11, 14.5, 9.94, 10));
        Assertions.assertTrue(classifier.isShootingStar());
    }

    @Test
    public void TestPriceActionClassifier_isBearishEngulfing() {
        RandomAccessBuffer<PriceBar> buffer = new RandomAccessBuffer<>(2);
        PriceActionClassifier classifier = new PriceActionClassifier(buffer);

        // empty state
        Assertions.assertFalse(classifier.isBearishEngulfing());

        // single bar
        buffer.add(getBar(9, 10, 9, 10));
        Assertions.assertFalse(classifier.isBearishEngulfing());

        // second bar, also up
        buffer.add(getBar(9.5, 10.5, 9.5, 10.5));
        Assertions.assertFalse(classifier.isBearishEngulfing());

        // third bar, down engulfing
        buffer.add(getBar(11, 11, 9.5, 9.5));
        Assertions.assertTrue(classifier.isBearishEngulfing());
    }

    @Test
    public void TestPriceActionClassifier_isDarkCloudCover() {
        RandomAccessBuffer<PriceBar> buffer = new RandomAccessBuffer<>(2);
        PriceActionClassifier classifier = new PriceActionClassifier(buffer);

        // empty state
        Assertions.assertFalse(classifier.isDarkCloudCover());

        // single bar
        buffer.add(getBar(9, 10, 9, 10));
        Assertions.assertFalse(classifier.isDarkCloudCover());

        // second bar, also up
        buffer.add(getBar(9.5, 10.5, 9.5, 10.5));
        Assertions.assertFalse(classifier.isDarkCloudCover());

        // third bar, down engulfing
        buffer.add(getBar(10.5, 10.5, 9.2, 10));
        Assertions.assertTrue(classifier.isDarkCloudCover());
    }
    
    @Test
    public void TestPriceActionClassifier_isTweezerTop() {
        RandomAccessBuffer<PriceBar> buffer = new RandomAccessBuffer<>(2);
        PriceActionClassifier classifier = new PriceActionClassifier(buffer);

        // empty state
        Assertions.assertFalse(classifier.isTweezerTop());

        // single bar
        buffer.add(getBar(9, 9, 10, 10));
        Assertions.assertFalse(classifier.isTweezerTop());

        // second bar, also up
        buffer.add(getBar(9, 10.5, 9.5, 9.5));
        Assertions.assertFalse(classifier.isTweezerTop());

        // third bar, up tweezer
        buffer.add(getBar(9.5, 10.5, 9, 9));
        Assertions.assertTrue(classifier.isTweezerTop());
    }

    @Test
    public void TestPriceActionClassifier_isEveningStar() {
        RandomAccessBuffer<PriceBar> buffer = new RandomAccessBuffer<>(3);
        PriceActionClassifier classifier = new PriceActionClassifier(buffer);

        // empty state
        Assertions.assertFalse(classifier.isEveningStar());

        // single bar
        buffer.add(getBar(9, 10, 9, 10));
        Assertions.assertFalse(classifier.isEveningStar());

        // second bar
        buffer.add(getBar(9, 9.04, 8.96, 9));
        Assertions.assertFalse(classifier.isEveningStar());

        // third bar, morning star
        buffer.add(getBar(10, 10, 9, 9));
        Assertions.assertTrue(classifier.isEveningStar());
    }

}
