package com.returnonintelligence.util;

import java.util.Random;

public class UniqueID {

    private static volatile long dividendID;
    private static volatile long claimID;

    /**
     * initialize.
     * Obtains dividendId an claimId values
     */
    public static void init(String dividendIDMarker, String claimIDMarker) {
        dividendID = Long.parseLong(dividendIDMarker);
        claimID = Long.parseLong(claimIDMarker);
    }

    /**
     *
     * @return unique 10 digits id, string
     */
    public synchronized static String getDividendID() {
        Long i;
        String s;
        int length;
        char[] c1 = new char[10];
        char[] c2;

        Random r = new Random();
        dividendID += r.nextInt(777);
        i = dividendID;
        s = i.toString();
        length = s.length();
        c2 = s.toCharArray();
        for (int j = 0; j < 10 - length; j++)
            c1[j] = '0';
        System.arraycopy(c2, 10 - length - 10 + length, c1, 10 - length, 10 - (10 - length));
        return new String(c1);
    }

    /**
     *
     * @return unique 10 digits id, string
     */
    public synchronized static String getClaimID() {
        Long i;
        String s;
        int length;
        char[] c1 = new char[10];
        char[] c2;

        Random r = new Random();
        claimID += r.nextInt(777);
        i = claimID;
        s = i.toString();
        length = s.length();
        c2 = s.toCharArray();
        for (int j = 0; j < 10 - length; j++)
            c1[j] = '0';
        System.arraycopy(c2, 10 - length - 10 + length, c1, 10 - length, 10 - (10 - length));
        return new String(c1);
    }
}
