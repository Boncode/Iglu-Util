package org.ijsberg.iglu.util.misc;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ShannonEntropy {

    private static String SOME_PASSWORD = "sfdgklj&32Z9*sH";//"ü";

    private static String SAMPLE = "1010010101010111010101001010101";//"ü";
    private static final String JWT =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9."
                    +
                    "eyJzdWJzY3JpcHRpb25faWQiOjaso,x o:;iJodXA5aWtuODFycjNiamNzZjZhanJ0ZG5jkjasdadMxMjcyMjc4NjYsImlhdCI6MTU0OTMwNDY2NiwiaXNzIjoib3MiLCJhdWQiOiJvbWFzX2NsaWVudHMifQ." +
                    "4-XcZvqJXyL4IWaBJH8JUiSiNZ6et2FBPS1ZGidmHi8";

    private static String TEXT = "There are ways to improve the performance of relative entropy when using it for detection. For many uses, it can be combined with other suspicious characteristics increase confidence in a signature. For example, you could look for an unsigned binary which makes a network connection to a domain with relative entropy over a certain threshold. Or a suspicious network connection made by a binary located in AppData. Red Canary does exactly this, looking for unsigned binaries located in AppData which make network connections made to domains with a relative entropy above 3.0.";

    @Test
    public void calculate() {
        //System.out.println(calculate(20, 0, 4 ,6, 7, 1, 2, 17));

        int[] array = new int[SAMPLE.length()];
        int count = 0;
/*        for(char c : SAMPLE.toCharArray()) {
            System.out.println(c * 1);
            array[count] = c;
        }
*/
        //System.out.println("1. " + calculate(256, array));
        System.out.println("1. " + calculateShannonEntropy(SAMPLE.toCharArray()));
        System.out.println("2. " + calculateShannonEntropy(SAMPLE.toCharArray()) / SAMPLE.length());

        System.out.println("3. " + calculateShannonEntropy(JWT.toCharArray()));
        System.out.println("4. " + calculateShannonEntropy(JWT.toCharArray()) / JWT.length());

        System.out.println("5. " + calculateShannonEntropy(TEXT.toCharArray()));
        System.out.println("6. " + calculateShannonEntropy(TEXT.toCharArray()) / TEXT.length());

        System.out.println("7. " + calculateShannonEntropy(SOME_PASSWORD.toCharArray()));
        System.out.println("8. " + calculateShannonEntropy(SOME_PASSWORD.toCharArray()) / SOME_PASSWORD.length());
    }


    public static Double calculateShannonEntropy(char[] values) {
        Map<Character, Integer> map = new HashMap<Character, Integer>();
        // count the occurrences of each value
        for (Character sequence : values) {
            if (!map.containsKey(sequence)) {
                map.put(sequence, 0);
            }
            map.put(sequence, map.get(sequence) + 1);
        }

        // calculate the entropy
        Double result = 0.0;
        for (Character sequence : map.keySet()) {
            Double frequency = (double) map.get(sequence) / values.length;
            result -= frequency * (Math.log(frequency) / Math.log(2));
        }

        return result;
    }


    public static double calculate(int m, int ... array) {

        // sequence of n integers are between 1 and m
        //int m = StdIn.readInt();

        // number of integers
        int n = array.length;

        // compute frequencies
        // freq[i] = # times integer i appears
        int[] freq = new int[m+1];
        for (int j = 0; j < n; j++) {
            int value = array[j];
            freq[value]++;
        }

        // compute Shannon entropy
        double entropy = 0.0;
        for (int i = 1; i <= m; i++) {
            double p = 1.0 * freq[i] / n;
            if (freq[i] > 0)
                entropy -= p * Math.log(p) / Math.log(2);
        }

        // print results
        return entropy;
    }
}