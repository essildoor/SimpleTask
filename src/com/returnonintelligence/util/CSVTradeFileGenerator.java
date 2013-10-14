package com.returnonintelligence.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class CSVTradeFileGenerator {

    private static SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
    private static String header = "Trade_id,Seller_acct,Buyer_acct,Amount,Trade_date,Settled_date";
    private static final int seed = 7323;
    private static Random r = new Random(seed);
    private static int tradeIdCounter = 1;

    private static String generateLine() {
        return generateTradeId() + "," + generateSellerAcct() + "," + generateBuyerAcct() + "," +
                generateAmount() + "," + generateTradeSettledDates();
    }

    private static String generateTradeId() {
        char[] c1 = new char[10];
        tradeIdCounter += r.nextInt(10);
        String tmp = String.valueOf(tradeIdCounter);
        for (int i = 0; i < 10 - tmp.length(); i++) {
            c1[i] = '0';
        }
        for (int i = 10 - tmp.length(); i < 10; i++) {
            c1[i] = tmp.charAt(i - 10 + tmp.length());
        }
        return new String(c1);
    }

    private static String generateSellerAcct() {
        return "00004276";
    }

    private static String generateBuyerAcct() {
        return "00002347";
    }

    private static int generateAmount() {
        return r.nextInt(9999);
    }

    private static Date generateRandomDate() {
        char[] c = new char[8];
        c[0] = (char) (r.nextInt(2) + 48);
        c[1] = (char) (r.nextInt(8) + 49);
        c[2] = '0';
        c[3] = (char) (r.nextInt(8) + 49);
        c[4] = '2';
        c[5] = '0';
        c[6] = '1';
        c[7] = '3';
        try {
            return format.parse(new String(c));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String generateTradeSettledDates() {
        Date tradeDate = generateRandomDate();
        Date settledDate = generateRandomDate();
        while (settledDate.compareTo(tradeDate) < 0)
            settledDate = generateRandomDate();
        return format.format(tradeDate) + "," + format.format(settledDate);
    }

    private static String generateExRecDates() {
        Date exDate = generateRandomDate();
        Date recDate = generateRandomDate();
        while (recDate.compareTo(exDate) < 0)
            recDate = generateRandomDate();
        return format.format(exDate) + "_" + format.format(recDate);
    }

    private static String generateFileName() {
         return generateShareName() + "_" + generateExRecDates()
                 + "_" + generateDividendPerShare() + ".csv";
    }

    private static String generateShareName() {
        char[] c = new char[8];
        c[0] = (char) (r.nextInt(25) + 65);
        c[1] = (char) (r.nextInt(25) + 65);
        c[2] = (char) (r.nextInt(9) + 48);
        c[3] = (char) (r.nextInt(9) + 48);
        c[4] = (char) (r.nextInt(9) + 48);
        c[5] = (char) (r.nextInt(9) + 48);
        c[6] = (char) (r.nextInt(9) + 48);
        c[7] = (char) (r.nextInt(9) + 48);
        return new String(c);
    }

    private static int generateDividendPerShare() {
        return r.nextInt(33);
    }

    public static void main(String[] args) {
        System.out.println("File generator launched");
        int lineNumber;
        File f;
        BufferedWriter writer;
        for (int i = 0; i < 1; i++) {
            lineNumber = r.nextInt(1000) + 1;
            f = new File("D:\\!_java_idea_projects\\SimpleTask\\!_work_dir\\stuff\\" + generateFileName());
            try {
                writer = new BufferedWriter(new FileWriter(f));
                writer.write(header + "\n");
                for (int j = 0; j < lineNumber; j++) {
                    writer.write(generateLine() + "\n");
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("File generator stopped");
    }
}
