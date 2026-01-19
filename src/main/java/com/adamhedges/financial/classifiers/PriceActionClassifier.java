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
        return false;
    }

    public boolean isPiercing() {
        return false;
    }

    public boolean isTweezerBottom() {
        return false;
    }

    public boolean isMorningStar() {
        return false;
    }

    public boolean isShootingStar() {
        return false;
    }

    public boolean isBearishEngulfing() {
        return false;
    }

    public boolean isDarkCloudCover() {
        return false;
    }

    public boolean isTweezerTop() {
        return false;
    }

    public boolean isEveningStar() {
        return false;
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
