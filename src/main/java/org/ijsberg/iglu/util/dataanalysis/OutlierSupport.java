package org.ijsberg.iglu.util.dataanalysis;

import java.util.List;

public class OutlierSupport {


    public static boolean naiveValueFitsTrend(float currentValue, List<Float> trend, float maxPercentageOffset) {
        float minVal = trend.get(trend.size()-1) * (1 - (maxPercentageOffset/100));
        float maxVal = trend.get(trend.size()-1) * (1 + (maxPercentageOffset/100));
        return minVal <= currentValue && currentValue <= maxVal;
    }

}
