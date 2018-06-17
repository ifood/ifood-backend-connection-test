package com.ifood.ifoodmanagement.domain;

import org.apache.commons.lang3.Range;

import java.util.Arrays;
import java.util.List;

public enum RestaurantRating {

    AWFUL,
    BAD,
    AVERAGE,
    GOOD,
    EXCELLENT;

    final static Range<Double> awfulRange = Range.between(0.0, 0.2);
    final static Range<Double> badRange = Range.between(0.21, 0.4);
    final static Range<Double> averageRange = Range.between(0.41, 0.6);
    final static Range<Double> goodRange = Range.between(0.61, 0.8);
    final static Range<Double> excellentRange = Range.between(0.81, 1.0);

    final static List<Range> ranges =
            Arrays.asList(awfulRange, badRange, averageRange, goodRange, excellentRange);

    public static RestaurantRating getRating(Double avgOffline){

        if (awfulRange.contains(avgOffline))
            return EXCELLENT;

        if (badRange.contains(avgOffline))
            return GOOD;

        if (averageRange.contains(avgOffline))
            return AVERAGE;

        if (goodRange.contains(avgOffline))
            return BAD;

        return AWFUL;
    }
}
