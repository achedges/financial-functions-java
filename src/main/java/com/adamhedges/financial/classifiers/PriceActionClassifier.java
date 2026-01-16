package com.adamhedges.financial.classifiers;

import com.adamhedges.financial.core.bars.PriceBar;
import com.adamhedges.financial.storage.buffers.RandomAccessBuffer;

import java.util.HashSet;
import java.util.Set;

public class PriceActionClassifier {

    public static boolean isHammer(RandomAccessBuffer<PriceBar> buffer) {
        return false;
    }

    public static boolean isBullishEngulfing(RandomAccessBuffer<PriceBar> buffer) {
        return false;
    }

    public static boolean isPiercing(RandomAccessBuffer<PriceBar> buffer) {
        return false;
    }

    public static boolean isTweezerBottom(RandomAccessBuffer<PriceBar> buffer) {
        return false;
    }

    public static boolean isMorningStar(RandomAccessBuffer<PriceBar> buffer) {
        return false;
    }

    public static boolean isShootingStar(RandomAccessBuffer<PriceBar> buffer) {
        return false;
    }

    public static boolean isBearishEngulfing(RandomAccessBuffer<PriceBar> buffer) {
        return false;
    }

    public static boolean isDarkCloudCover(RandomAccessBuffer<PriceBar> buffer) {
        return false;
    }

    public static boolean isTweezerTop(RandomAccessBuffer<PriceBar> buffer) {
        return false;
    }

    public static boolean isEveningStar(RandomAccessBuffer<PriceBar> buffer) {
        return false;
    }

    public static Set<PriceAction> classifyPriceAction(RandomAccessBuffer<PriceBar> buffer) {
        Set<PriceAction> actions = new HashSet<>();

        if (isHammer(buffer)) {
            actions.add(PriceAction.Hammer);
        }

        if (isBullishEngulfing(buffer)) {
            actions.add(PriceAction.BullishEngulfing);
        }

        if (isPiercing(buffer)) {
            actions.add(PriceAction.Piercing);
        }

        if (isTweezerBottom(buffer)) {
            actions.add(PriceAction.TweezerBottom);
        }

        if (isMorningStar(buffer)) {
            actions.add(PriceAction.MorningStar);
        }

        if (isShootingStar(buffer)) {
            actions.add(PriceAction.ShootingStar);
        }

        if (isBearishEngulfing(buffer)) {
            actions.add(PriceAction.BearishEngulfing);
        }

        if (isDarkCloudCover(buffer)) {
            actions.add(PriceAction.DarkCloudCover);
        }

        if (isTweezerTop(buffer)) {
            actions.add(PriceAction.TweezerTop);
        }

        if (isEveningStar(buffer)) {
            actions.add(PriceAction.EveningStar);
        }

        return actions;
    }

}
