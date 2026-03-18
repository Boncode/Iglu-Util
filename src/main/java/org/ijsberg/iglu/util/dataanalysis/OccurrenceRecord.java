package org.ijsberg.iglu.util.dataanalysis;

import org.ijsberg.iglu.util.time.TimePeriod;
import org.ijsberg.iglu.util.time.TimeUnit;

import java.util.ArrayList;

import static org.ijsberg.iglu.util.time.TimeUnit.INDEFINITE;

public class OccurrenceRecord {

    private record Occurrence (Long timeMs, String remark) {}

    private static final ArrayList<Occurrence> occurrences = new ArrayList<>();

    private TimeUnit storagePeriod = INDEFINITE;

    private long latestOccurrence;

    public void recordOccurrence(String remark) {
        synchronized (occurrences) {
            latestOccurrence = System.currentTimeMillis();
            occurrences.add(0, new Occurrence(latestOccurrence,remark));
        }
    }

    public boolean isNrOccurrencesBelow(int threshold, TimePeriod window) {
        synchronized (occurrences) {
            if (occurrences.size() < threshold) {
                return true;
            }
        }
        long now = System.currentTimeMillis();
        long windowStartTime = now - window.getDurationInMs();
        if (latestOccurrence < windowStartTime) {
            return true;
        }
        synchronized (occurrences) {
            int count = 0;
            for (Occurrence occurrence : occurrences) {
                if (occurrence.timeMs > windowStartTime) {
                    count++;
                    if (count >= threshold) {
                        return false;
                    }
                } else {
                    break;
                }
            }
            return true;
        }
    }


    public int getNrOccurrences() {
        return occurrences.size();
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
