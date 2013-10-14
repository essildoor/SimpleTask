package com.returnonintelligence.fileio;

import com.returnonintelligence.datatypes.TradePackage;
import com.returnonintelligence.datatypes.TradeRecord;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class CSVTradeFileReader implements Reader {

    private List<TradeRecord> tradeRecordsList;
    private String fileName;
    private String shareName;
    private Date exDate;
    private Date recDate;
    private int dividendAmountPerShare;

    /**
     * Constructor
     *
     * @param fileName - file name
     */
    public CSVTradeFileReader(String fileName) {
        this.fileName = fileName;
        tradeRecordsList = new ArrayList<TradeRecord>();
    }

    /**
     * Retrieves TradeRecords from specified file
     *
     * @return TradePackage instance
     */
    public TradePackage read() {
        parseFileName();
        readData();
        TradePackage result = null;
        try {
            result = new TradePackage(shareName, exDate, recDate, dividendAmountPerShare,
                    tradeRecordsList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Retrieves shareName, exDate, recDate and dividendAmountPerShare from fileName
     */
    private void parseFileName() {
        StringTokenizer st = new StringTokenizer(fileName, "\\");
        String s = "";
        while (st.hasMoreTokens())
            s = st.nextToken();
        st = new StringTokenizer(s, "_.");
        shareName = st.nextToken();
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
        try {
            exDate = format.parse(st.nextToken());
            recDate = format.parse(st.nextToken());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dividendAmountPerShare = Integer.parseInt(st.nextToken());
    }

    /**
     * Retrieves data from file, creates instances of TradeRecord and stores them into
     * tradeRecordsList
     * Reads data according sequence of field names in header
     */
    private void readData() {

        String[] header = new String[6];
        String[] l = new String[6];
        StringTokenizer st;
        String line;
        int tradeIdPos = 0, sellerAcctPos = 0, buyerAcctPos = 0, amountPos = 0, tdPos = 0, sdPos = 0;
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            //reads header
            st = new StringTokenizer(reader.readLine(), ",");
            if (st.countTokens() != 6)
                throw new IOException("Incorrect header in file " + fileName);
            for (int i = 0; i < 6; i++) {
                header[i] = st.nextToken();
                if (header[i].equals("Trade_id")) {
                    tradeIdPos = i;
                    continue;
                }
                if (header[i].equals("Seller_acct")) {
                    sellerAcctPos = i;
                    continue;
                }
                if (header[i].equals("Buyer_acct")) {
                    buyerAcctPos = i;
                    continue;
                }
                if (header[i].equals("Amount")) {
                    amountPos = i;
                    continue;
                }
                if (header[i].equals("Trade_date")) {
                    tdPos = i;
                    continue;
                }
                if (header[i].equals("Settled_date")) {
                    sdPos = i;
                }
            }
            if (tradeIdPos == 0 && sellerAcctPos == 0 && buyerAcctPos == 0 && amountPos == 0 &&
                    tdPos == 0 && sdPos == 0) {
                throw new IOException("Incorrect header in file " + fileName);
            }
            //reads data according header sequence
            while ((line = reader.readLine()) != null) {
                st = new StringTokenizer(line, ",");
                if (st.countTokens() != 6)
                    throw new IOException("Incorrect data line in file " + fileName);
                for (int i = 0; i < 6; i++) {
                    l[i] = st.nextToken();
                }
                try {
                    tradeRecordsList.add(new TradeRecord(l[tradeIdPos], l[sellerAcctPos],
                            l[buyerAcctPos], Integer.parseInt(l[amountPos]), format.parse(l[tdPos]),
                            format.parse(l[sdPos])));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
