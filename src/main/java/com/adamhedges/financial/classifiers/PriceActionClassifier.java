package com.adamhedges.financial.classifiers;

import com.adamhedges.financial.core.bars.PriceBar;
import com.adamhedges.financial.storage.buffers.RandomAccessBuffer;
import com.adamhedges.financial.utils.ComparisonUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PriceActionClassifier {

    private final RandomAccessBuffer<PriceBar> buffer;

    public PriceActionClassifier(RandomAccessBuffer<PriceBar> buffer) {
        this.buffer = buffer;
    }

    public boolean isHammer() {
        Optional<PriceBar> optionalBar = buffer.getLast();
        if (optionalBar.isEmpty()) {
            return false;
        }

        PriceBar bar = optionalBar.get();
        if (bar.isDown()) {
            return false;
        }

        double lowWickMultiple = bar.getLowWick() / bar.getBody(); // look for > 3.0
        double highWickMultiple = bar.getHighWick() / bar.getRange(); // look for < 0.05
        double bodyMultiple = bar.getBody() / bar.getRange(); // look for 0.1 < m < 0.2

        return lowWickMultiple > 3.0 && highWickMultiple < 0.05 && ComparisonUtils.between(bodyMultiple, 0.1, 0.25);
    }

    public boolean isBullishEngulfing() {
        Optional<PriceBar> firstBar = buffer.getLast(1);
        Optional<PriceBar> secondBar = buffer.getLast();

        // need both bars
        if (firstBar.isEmpty() || secondBar.isEmpty()) {
            return false;
        }

        // 1st bar should be down, second should be up
        if (firstBar.get().isUp() || secondBar.get().isDown()) {
            return false;
        }

        boolean closesAboveHigh = secondBar.get().getClose() >= firstBar.get().getHigh();
        boolean opensBelowClose = secondBar.get().getOpen() <= firstBar.get().getClose();

        return closesAboveHigh && opensBelowClose;
    }

    public boolean isPiercing() {
        Optional<PriceBar> firstBar = buffer.getLast(1);
        Optional<PriceBar> secondBar = buffer.getLast();

        // need both bars
        if (firstBar.isEmpty() || secondBar.isEmpty()) {
            return false;
        }

        // 1st bar should be down, second bar should be up
        if (firstBar.get().isUp() || secondBar.get().isDown()) {
            return false;
        }

        PriceBar b1 = firstBar.get();
        PriceBar b2 = secondBar.get();

        double b1_mid = (b1.getOpen() + b1.getClose()) / 2;
        boolean closesAboveMidpoint = ComparisonUtils.between(b2.getClose(), b1_mid, b1.getOpen());
        boolean opensBelowClose = b2.getOpen() <= b1.getClose();

        return closesAboveMidpoint && opensBelowClose;
    }

    public boolean isTweezerBottom() {
        Optional<PriceBar> firstBar = buffer.getLast(1);
        Optional<PriceBar> secondBar = buffer.getLast();

        // need both bars
        if (firstBar.isEmpty() || secondBar.isEmpty()) {
            return false;
        }

        // 1st bar should be down, second bar should be up
        if (firstBar.get().isUp() || secondBar.get().isDown()) {
            return false;
        }

        PriceBar b1 = firstBar.get();
        PriceBar b2 = secondBar.get();

        // body is at least 20% of range
        // low wick is at least 1x of body
        double b1_range_mult = b1.getRange() / b1.getBody();
        double b2_range_mult = b2.getRange() / b2.getBody();
        double b1_low_mult = b1.getLowWick() / b1.getBody();
        double b2_low_mult = b2.getLowWick() / b2.getBody();

        return b1_range_mult >= 0.2 && b2_range_mult >= 0.2 && b1_low_mult >= 1.0 && b2_low_mult >= 1.0;
    }

    public boolean isMorningStar() {
        Optional<PriceBar> firstBar = buffer.getLast(2);
        Optional<PriceBar> secondBar = buffer.getLast(1);
        Optional<PriceBar> thirdBar = buffer.getLast();

        // need three bars
        if (firstBar.isEmpty() || secondBar.isEmpty() || thirdBar.isEmpty()) {
            return false;
        }

        // 1st bar should be down, third bar should be up
        if (firstBar.get().isUp() || thirdBar.get().isDown()) {
            return false;
        }

        PriceBar b1 = firstBar.get();
        PriceBar b2 = secondBar.get();
        PriceBar b3 = thirdBar.get();

        // b2 range is < 0.1 of b1 range
        // b3 engulfs b1
        double b2_range_mult = b2.getRange() / b1.getRange();
        boolean closesAboveOpen = b3.getClose() >= b1.getOpen();
        boolean opensBelowClose = b3.getOpen() <= b1.getClose();

        return b2_range_mult <= 0.1 && closesAboveOpen && opensBelowClose;
    }

    public boolean isShootingStar() {
        Optional<PriceBar> optionalBar = buffer.getLast();
        if (optionalBar.isEmpty()) {
            return false;
        }

        PriceBar bar = optionalBar.get();
        if (bar.isUp()) {
            return false;
        }

        double high_wick_mult = bar.getHighWick() / bar.getBody(); // look for > 3.0
        double low_wick_mult = bar.getLowWick() / bar.getRange(); // look for < 0.05
        double body_mult = bar.getBody() / bar.getRange(); // look for 0.1 < m < 0.2

        return high_wick_mult > 3.0 && low_wick_mult < 0.05 && ComparisonUtils.between(body_mult, 0.1, 0.25);
    }

    public boolean isBearishEngulfing() {
        Optional<PriceBar> firstBar = buffer.getLast(1);
        Optional<PriceBar> secondBar = buffer.getLast();

        // need both bars
        if (firstBar.isEmpty() || secondBar.isEmpty()) {
            return false;
        }

        // 1st bar should be up, second bar should be down
        if (firstBar.get().isDown() || secondBar.get().isUp()) {
            return false;
        }

        boolean closesBelowLow = secondBar.get().getClose() <= firstBar.get().getLow();
        boolean opensAboveClose = secondBar.get().getOpen() >= firstBar.get().getClose();

        return closesBelowLow && opensAboveClose;
    }

    public boolean isDarkCloudCover() {
        Optional<PriceBar> firstBar = buffer.getLast(1);
        Optional<PriceBar> secondBar = buffer.getLast();

        // need both bars
        if (firstBar.isEmpty() || secondBar.isEmpty()) {
            return false;
        }

        // 1st bar should be up, second bar should be down
        if (firstBar.get().isDown() || secondBar.get().isUp()) {
            return false;
        }

        PriceBar b1 = firstBar.get();
        PriceBar b2 = secondBar.get();

        double b1_mid = (b1.getClose() + b1.getOpen()) / 2;
        boolean closesBelowMidpoint = ComparisonUtils.between(b2.getClose(), b1.getOpen(), b1_mid);
        boolean opensAboveClose = b2.getOpen() >= b1.getClose();

        return closesBelowMidpoint && opensAboveClose;
    }

    public boolean isTweezerTop() {
        Optional<PriceBar> firstBar = buffer.getLast(1);
        Optional<PriceBar> secondBar = buffer.getLast();

        // need both bars
        if (firstBar.isEmpty() || secondBar.isEmpty()) {
            return false;
        }

        // 1st bar should be up, second bar should be down
        if (firstBar.get().isDown() || secondBar.get().isUp()) {
            return false;
        }

        PriceBar b1 = firstBar.get();
        PriceBar b2 = secondBar.get();

        // body is at least 20% of range
        // low wick is at least 1x of body
        double b1_range_mult = b1.getRange() / b1.getBody();
        double b2_range_mult = b2.getRange() / b2.getBody();
        double b1_high_mult = b1.getHighWick() / b1.getBody();
        double b2_high_mult = b2.getHighWick() / b2.getBody();

        return b1_range_mult >= 0.2 && b2_range_mult >= 0.2 && b1_high_mult >= 1.0 && b2_high_mult >= 1.0;
    }

    public boolean isEveningStar() {
        Optional<PriceBar> firstBar = buffer.getLast(2);
        Optional<PriceBar> secondBar = buffer.getLast(1);
        Optional<PriceBar> thirdBar = buffer.getLast();

        // need three bars
        if (firstBar.isEmpty() || secondBar.isEmpty() || thirdBar.isEmpty()) {
            return false;
        }

        // 1st bar should be up, third bar should be down
        if (firstBar.get().isDown() || thirdBar.get().isUp()) {
            return false;
        }

        PriceBar b1 = firstBar.get();
        PriceBar b2 = secondBar.get();
        PriceBar b3 = thirdBar.get();

        // b2 range is < 0.1 of b1 range
        // b3 engulfs b1
        double b2_range_mult = b2.getRange() / b1.getRange();
        boolean opensAboveClose = b3.getOpen() >= b1.getClose();
        boolean closesBelowOpen = b3.getClose() <= b1.getOpen();

        return b2_range_mult <= 0.1 && closesBelowOpen && opensAboveClose;
    }

    public Set<PriceAction> classifyPriceAction() {
        Set<PriceAction> actions = new HashSet<>();

        if (isHammer()) {
            actions.add(PriceAction.Hammer);
        }

        if (isBullishEngulfing()) {
            actions.add(PriceAction.BullishEngulfing);
        }

        if (isPiercing()) {
            actions.add(PriceAction.Piercing);
        }

        if (isTweezerBottom()) {
            actions.add(PriceAction.TweezerBottom);
        }

        if (isMorningStar()) {
            actions.add(PriceAction.MorningStar);
        }

        if (isShootingStar()) {
            actions.add(PriceAction.ShootingStar);
        }

        if (isBearishEngulfing()) {
            actions.add(PriceAction.BearishEngulfing);
        }

        if (isDarkCloudCover()) {
            actions.add(PriceAction.DarkCloudCover);
        }

        if (isTweezerTop()) {
            actions.add(PriceAction.TweezerTop);
        }

        if (isEveningStar()) {
            actions.add(PriceAction.EveningStar);
        }

        return actions;
    }

}
