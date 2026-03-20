package org.ijsberg.iglu.util.misc;

import junit.framework.TestCase;

public class NumberSupportTest extends TestCase {

    public void testIsLargerThan() {
        float a = 1.666f;
        float b = 1.666f;
        assertFalse(NumberSupport.isLargerThan(a, b, .00001d));

    }
}