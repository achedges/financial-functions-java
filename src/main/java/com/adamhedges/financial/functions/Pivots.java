package com.adamhedges.financial.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class Pivots {

    public static final int DEFAULT_DISPERSION_GROUPS = 4;
    public static final int DEFAULT_CONSOLIDATION_GROUPS = 4;

    public static <T> List<Integer> getPivots(List<T> values, BiFunction<T, T, Boolean> comparison) {

        // Find all elements where the provided comparison bi-function is true for all non-null neighbors, and return their indices

        int nbars = values.size();
        List<Integer> pivots = new ArrayList<>();

        for (int i = 0; i < nbars; i++) {
            T c = values.get(i);
            T l = i == 0 ? null : values.get(i-1);
            T r = i == nbars - 1 ? null : values.get(i+1);

            if (l == null && r == null) {
                return pivots;
            } else if (l != null & r != null) {
                if (comparison.apply(c, l) && comparison.apply(c, r)) {
                    pivots.add(i);
                }
            } else if (l == null && comparison.apply(c, r)) {
                pivots.add(i);
            } else if (r == null && comparison.apply(c, l)) {
                pivots.add(i);
            }
        }

        return pivots;

    }

    public static <T> List<Double> getDispersions(List<T> values, List<Integer> pivotIndexes, int span, BiFunction<T, T, Double> diff) {

        // Given a list of values, and a corresponding list of pivots (from getPivots()), find the dispersion of each pivot point
        // by calculating the maximum difference between that pivot point and the neighboring elements, up to some maximum span.

        int nbars = values.size();
        List<Double> dispersions = new ArrayList<>(pivotIndexes.size());

        for (Integer pivotIndex : pivotIndexes) {
            double dispersion = 0.0;
            T peakBar = values.get(pivotIndex);

            for (int i = 1; i <= span; i++) {
                double leftspan = pivotIndex - i < 0 ? 0.0 : diff.apply(peakBar, values.get(pivotIndex - i));
                double rightspan = pivotIndex + i >= nbars ? 0.0 : diff.apply(peakBar, values.get(pivotIndex + i));
                double maxspan = Math.max(leftspan, rightspan);
                if (maxspan > dispersion) {
                    dispersion = maxspan;
                }
            }

            dispersions.add(dispersion);
        }

        return dispersions;

    }

    public static List<Integer> consolidatePivots(List<Integer> pivotIndexes, List<Double> dispersions, int span) {

        // Given a list of pivot indices (from getPivots()) and dispersions (from getDispersions()), produce a consolidated
        // list of pivot indices by collapsing neighboring pivot points into the pivot point with the highest dispersion,
        // up to some maximum span.

        if (pivotIndexes.size() != dispersions.size()) {
            throw new RuntimeException("'pivotIndexes' and 'dispersions' arguments are not the same size");
        }

        List<Integer> consolidated = new ArrayList<>(pivotIndexes);
        for (int i = 0; i < consolidated.size() - 1; i++) {
            for (int j = i + 1; j < consolidated.size(); j++) {
                if (pivotIndexes.get(j) - pivotIndexes.get(i) > span) {
                    break;
                }

                if (consolidated.get(j) == -1) {
                    continue;
                }

                if (dispersions.get(i) > dispersions.get(j)) {
                    consolidated.set(j, -1);
                } else {
                    consolidated.set(i, -1);
                    break;
                }
            }
        }

        return consolidated.stream().filter(c -> c >= 0).toList();

    }

    public static <T> List<Integer> get(
        List<T> values,
        BiFunction<T, T, Boolean> comparison,
        BiFunction<T, T, Double> dispersion
    ) {
        int dispersionSpan = values.size() / DEFAULT_DISPERSION_GROUPS;
        int consolidationSpan = values.size() / DEFAULT_CONSOLIDATION_GROUPS;
        return Pivots.get(values, comparison, dispersion, dispersionSpan, consolidationSpan);
    }

    public static <T> List<Integer> get(
        List<T> values,
        BiFunction<T, T, Boolean> comparison,
        BiFunction<T, T, Double> dispersion,
        int dispersionSpan,
        int consolidationSpan
    ) {
        List<Integer> rawPivots = Pivots.getPivots(values, comparison);
        List<Double> dispersions = Pivots.getDispersions(values, rawPivots, dispersionSpan, dispersion);
        return Pivots.consolidatePivots(rawPivots, dispersions, consolidationSpan);
    }

}
