package org.ijsberg.iglu.util.dataanalysis;


import org.ijsberg.iglu.util.time.TimePeriod;
import org.ijsberg.iglu.util.time.TimeUnit;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OccurrenceRecordTest {

    @Test
    public void testIsNrOccurrencesBelow() throws Exception {
        OccurrenceRecord record = new OccurrenceRecord();
        record.recordOccurrence("bla");
        Thread.sleep(1000);
        record.recordOccurrence("bla");
        record.recordOccurrence("bla");
        record.recordOccurrence("bla");
        Thread.sleep(1000);
        assertTrue(record.isNrOccurrencesBelow(5, new TimePeriod(5, TimeUnit.SECOND)));
        assertFalse(record.isNrOccurrencesBelow(4, new TimePeriod(5, TimeUnit.SECOND)));
    }
}