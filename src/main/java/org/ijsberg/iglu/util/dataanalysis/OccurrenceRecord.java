package org.ijsberg.iglu.util.dataanalysis;

import org.ijsberg.iglu.util.time.TimeUnit;

import java.util.ArrayList;

import static org.ijsberg.iglu.util.time.TimeUnit.INDEFINITE;

public class OccurrenceRecord {

    private ArrayList<Long> occurrences = new ArrayList<>();

    private TimeUnit storagePeriod = INDEFINITE;

    public void recordOccurrence() {
        occurrences.add(System.currentTimeMillis());
    }

    public boolean isNrOccurrencesBelow(int threshold, TimeUnit window) {
        if(occurrences.size() < threshold) {
            return false;
        }
        return true;
    }


    public float getNrOccurrences(TimeUnit frequencyDenominator, TimeUnit window) {
        int count = 0;
        double total = 0;
        for (Long occurrence : occurrences) {}
        return 0.0f;
    }


    /*
    public boolean isRollingAverageBelow(float threshold, TimeUnit frequencyDenominator, TimeUnit window) {
        if(occurrences.size() < threshold) {
            return false;
        }
        return true;
    }


    public float getRollingAverage(TimeUnit frequencyDenominator, TimeUnit window) {
        int count = 0;
        double total = 0;
        for (Long occurrence : occurrences) {}
        return 0.0f;
    }
*/
}
