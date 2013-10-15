package com.returnonintelligence.util;

import com.returnonintelligence.datatypes.*;
import com.returnonintelligence.io.CSVTradeFileReader;
import com.returnonintelligence.io.DBTradeDataWriter;
import com.returnonintelligence.io.GeneralWriter;
import com.returnonintelligence.io.XMLTradeFileReader;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Processes input data either by full file (TradePackage instance) or by line from file (TradeRecord instance)
 */
@SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
public class PackageProcessor implements Runnable {

    private String tradeFileName;
    private String shortTradeFileName;
    private String processedFileName;
    private String dividendFileName;
    private String claimFileName;
    private String shareName;
    private int dividendsPerShare;
    private Date exDate;
    private Date recDate;
    private Map<String, Integer> fieldSequenceInTradeFile = new HashMap<String, Integer>(6);


    public PackageProcessor(String tradeFileName) {
        this.shortTradeFileName = tradeFileName;
        this.tradeFileName = Props.getInputFolderPath() + tradeFileName;
        this.processedFileName = Props.getProcessedFolderPath() + "PROCESSED_" + tradeFileName;
        this.dividendFileName = Props.getOutputFolderPath() + "DIV_" + tradeFileName;
        this.claimFileName = Props.getOutputFolderPath() + "CLAIMS_" + tradeFileName;
    }

    /**
     * Parses trade file name and retrieves shareName, exDate, recDate and dividends amount per share
     */
    private void parseTradeDataFileName() {
        StringTokenizer st = new StringTokenizer(shortTradeFileName, "_.");
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
        shareName = st.nextToken();
        try {
            exDate = format.parse(st.nextToken());
            recDate = format.parse(st.nextToken());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dividendsPerShare = Integer.parseInt(st.nextToken());
    }

    /**
     * processes data by whole file:
     * <p/>
     * 1) reads data from trade file to TradePackage instance
     * 2) processes data and creates DividendPackage and ClaimPackage if it it needed
     * 3) writes new and input data to specified folders
     */
    private void processByFile() {
        TradePackage tradePackage;
        DividendRecord dividendRecord;
        ClaimRecord claimRecord;
        List<DividendRecord> dividendRecordList = new ArrayList<DividendRecord>();
        List<ClaimRecord> claimRecordList = new ArrayList<ClaimRecord>();
        GeneralWriter writer;
        StringTokenizer st = new StringTokenizer(shortTradeFileName, ".");
        st.nextToken();
        String tradeFileType = st.nextToken();
        if (tradeFileType.equals("csv")) {
            CSVTradeFileReader reader = new CSVTradeFileReader(tradeFileName);
            tradePackage = reader.read();
            DBTradeDataWriter dbWriter = new DBTradeDataWriter(tradePackage);
            dbWriter.write();
        } else if (tradeFileType.equals("xml")) {
            XMLTradeFileReader reader = new XMLTradeFileReader(tradeFileName);
            tradePackage = reader.read();
            //write trade data to MySQL DB
            DBTradeDataWriter dbWriter = new DBTradeDataWriter(tradePackage);
            dbWriter.write();
        } else {
            System.out.println(shortTradeFileName + " has an unknown extension!");
            return;
        }
        //Reads data from trade file
        parseTradeDataFileName();
        //processes file data
        for (TradeRecord tradeRecord : tradePackage.getDataList()) {
            dividendRecord = createDividendRecord(tradeRecord);
            dividendRecordList.add(dividendRecord);
            if ((claimRecord = createClaimRecord(tradeRecord, dividendRecord))
                    != null) {
                claimRecordList.add(claimRecord);
            }
        }

        //writes processed file
        AbstractPackage dividendPackage = new DividendPackage(dividendRecordList);
        writer = new GeneralWriter(processedFileName, tradePackage);
        writer.write();

        //writes dividend file
        writer = new GeneralWriter(dividendFileName, dividendPackage);
        writer.write();

        //writes claim file if at lest one claim exists
        if (!claimRecordList.isEmpty()) {
            AbstractPackage claimPackage = new ClaimPackage(claimRecordList);
            writer = new GeneralWriter(claimFileName, claimPackage);
            writer.write();
        }
    }

    /**
     * Generates ClaimRecord instance either exDate >= tradeDate and recDate < settledDate or
     * exDate < tradeDate and recDate >= settledDate, otherwise returns null
     *
     * @param tradeRecord    - trade record to be processed
     * @param dividendRecord - dividend record to be processed
     * @return ClaimRecord instance
     */
    private ClaimRecord createClaimRecord(TradeRecord tradeRecord, DividendRecord dividendRecord) {

        int amount = tradeRecord.getAmount();
        String tradeId = tradeRecord.getTradeId();
        String recAcct = dividendRecord.getAcct();
        String entAcct;
        Date tradeDate = tradeRecord.getTradeDate();

        if (exDate.compareTo(tradeDate) < 0) {
            entAcct = tradeRecord.getSellerAcct();
            if (!entAcct.equals(recAcct)) {
                return new ClaimRecord(UniqueID.getClaimID(), tradeId, recAcct, entAcct, amount);
            }
        } else {
            entAcct = tradeRecord.getBuyerAcct();
            if (!entAcct.equals(recAcct)) {
                return new ClaimRecord(UniqueID.getClaimID(), tradeId, recAcct, entAcct, amount);
            }
        }
        return null;
    }

    /**
     * Generates DividendRecord instance
     *
     * @param tradeRecord - trade record to be processed
     * @return DividendRecord instance
     */
    private DividendRecord createDividendRecord(TradeRecord tradeRecord) {
        String divId = UniqueID.getDividendID();
        String tradeId = tradeRecord.getTradeId();
        String acct;
        int amount = tradeRecord.getAmount();
        if (recDate.compareTo(tradeRecord.getSettledDate()) < 0) {
            acct = tradeRecord.getSellerAcct();
        } else {
            acct = tradeRecord.getBuyerAcct();
        }

        return new DividendRecord(divId, tradeId, acct, amount);
    }

    private void processByLine() {
        String tradeFileExtension;
        StringTokenizer st = new StringTokenizer(shortTradeFileName, ".");
        st.nextToken();
        tradeFileExtension = st.nextToken();

        if (tradeFileExtension.equals("csv")) {

            int tradeIdPos;
            int sellerAcctPos;
            int buyerAcctPos;
            int amountPos;
            int tradeDatePos;
            int settledDatePos;
            TradeRecord tradeRecord = null;
            DividendRecord dividendRecord = null;
            ClaimRecord claimRecord = null;
            String[] token = new String[6];
            String line;
            SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");

            parseTradeDataFileName();

            try {
                BufferedReader tradeDataReader =
                        new BufferedReader(new FileReader(tradeFileName));
                BufferedWriter processedDataWriter =
                        new BufferedWriter(new FileWriter(processedFileName));
                BufferedWriter dividendDataWriter =
                        new BufferedWriter(new FileWriter(dividendFileName));
                BufferedWriter claimDataWriter = null;

                setFieldSequenceInTradeFile(tradeDataReader.readLine());

                tradeIdPos = fieldSequenceInTradeFile.get("Trade_id");
                sellerAcctPos = fieldSequenceInTradeFile.get("Seller_acct");
                buyerAcctPos = fieldSequenceInTradeFile.get("Buyer_acct");
                amountPos = fieldSequenceInTradeFile.get("Amount");
                tradeDatePos = fieldSequenceInTradeFile.get("Trade_date");
                settledDatePos = fieldSequenceInTradeFile.get("Settled_date");

                processedDataWriter.write(
                        "Trade_id,Seller_acct,Buyer_acct,Amount,Trade_date,Settled_date\n");
                dividendDataWriter.write("Dividend_id,Trade_id,Account,Amount\n");

                while ((line = tradeDataReader.readLine()) != null) {
                    st = new StringTokenizer(line, ",");
                    //reads one line from trade file
                    for (int i = 0; i < 6; i++) {
                        token[i] = st.nextToken();
                    }
                    //creates TradeRecord
                    tradeRecord = new TradeRecord(token[tradeIdPos], token[sellerAcctPos],
                            token[buyerAcctPos], Integer.parseInt(token[amountPos]),
                            format.parse(token[tradeDatePos]), format.parse(token[settledDatePos]));
                    //writes read line to processed file
                    processedDataWriter.write(tradeRecord.toString() + "\n");
                    //processes trade record and produces dividend record and if it needs claim record
                    dividendRecord = createDividendRecord(tradeRecord);
                    //writes dividend data to dividend file
                    dividendDataWriter.write(dividendRecord.toString() + "\n");
                    claimRecord = createClaimRecord(tradeRecord, dividendRecord);
                    if (claimRecord != null) {
                        if (claimDataWriter == null) {
                            claimDataWriter = new BufferedWriter(new FileWriter(claimFileName));
                            claimDataWriter.write("Claim_id,Trade_id,Rec_acct,Ent_acct,Amount\n");
                        }
                        claimDataWriter.write(claimRecord.toString() + "\n");
                    }
                }
                tradeDataReader.close();
                processedDataWriter.close();
                dividendDataWriter.close();
                if (claimDataWriter != null)
                    claimDataWriter.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (tradeFileExtension.equals("xml")) {
            processByFile();
        } else {
            System.out.println(shortTradeFileName + " has an unknown extension!");
            return;
        }
    }

    /**
     * parses trade data header and stores fields position into HashMap
     *
     * @param header - first line from trade data file
     */
    private void setFieldSequenceInTradeFile(String header) {
        String token;
        String tradeId = "Trade_id";
        String sellerAcct = "Seller_acct";
        String buyerAcct = "Buyer_acct";
        String amount = "Amount";
        String tradeDate = "Trade_date";
        String settledDate = "Settled_date";
        StringTokenizer st = new StringTokenizer(header, ",");

        for (int i = 0; i < 6; i++) {
            token = st.nextToken();
            if (token.equals(tradeId)) {
                fieldSequenceInTradeFile.put(tradeId, i);
                continue;
            }
            if (token.equals(sellerAcct)) {
                fieldSequenceInTradeFile.put(sellerAcct, i);
                continue;
            }
            if (token.equals(buyerAcct)) {
                fieldSequenceInTradeFile.put(buyerAcct, i);
                continue;
            }
            if (token.equals(amount)) {
                fieldSequenceInTradeFile.put(amount, i);
                continue;
            }
            if (token.equals(tradeDate)) {
                fieldSequenceInTradeFile.put(tradeDate, i);
                continue;
            }
            if (token.equals(settledDate)) {
                fieldSequenceInTradeFile.put(settledDate, i);
            }
        }
        if (fieldSequenceInTradeFile.isEmpty()) {
            System.out.println("an error occurred during parsing file header! file name: " +
                    shortTradeFileName);
        }
    }


    @Override
    public void run() {
        if (Props.getProcessingType().equals("byLine")) {
            processByLine();
        }
        else {
            processByFile();

        }
        new File(tradeFileName).delete();
        System.out.println(shortTradeFileName + " successfully processed");
    }
}
