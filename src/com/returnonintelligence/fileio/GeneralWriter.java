package com.returnonintelligence.fileio;


import com.returnonintelligence.datatypes.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

public class GeneralWriter implements Writer {

    private String fileName;
    private AbstractPackage pack;

    public GeneralWriter(String fileName, AbstractPackage pack) {
        this.fileName = fileName;
        this.pack = pack;
    }

    public void write() {
        StringTokenizer st = new StringTokenizer(fileName, ".");
        String extension = "";
        while (st.hasMoreTokens()) {
            extension = st.nextToken();
        }
        if (extension.equals("csv")) {
            writeCSV();
            return;
        }
        if (extension.equals("xml")) {
            writeXML();
            return;
        }
        System.out.println("Failed to write " + fileName);
    }

    private void writeXML() {
        String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        String space = "    ";
        int packType = pack.getPACK_TYPE();

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(xmlHeader + "\n");

            writer.write("<root>\n");

            for (Record record : pack.getDataList()) {
                writer.write(recordToXml(record, packType));
            }

            writer.write("</root>");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param record     Record instance to be converted to String instance (xml format)
     * @param recordType - 0 if TradeRecord, 1 if DividendRecord, 2 if ClaimRecord
     * @return xml representation of record (String)
     */
    private String recordToXml(Record record, int recordType) {
        String result = null;
        String space = "    ";
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");

        switch (recordType) {
            case 0: {
                result = space + "<row>\n" +
                        space + space +
                        "<Trade_id>" + ((TradeRecord) record).getTradeId() + "</Trade_id>\n" +
                        space + space +
                        "<Seller_acct>" + ((TradeRecord) record).getSellerAcct() + "</Seller_acct>\n" +
                        space + space +
                        "<Buyer_acct>" + ((TradeRecord) record).getBuyerAcct() + "</Buyer_acct>\n" +
                        space + space +
                        "<Amount>" + ((TradeRecord) record).getAmount() + "</Amount>\n" +
                        space + space +
                        "<Trade_date>" + format.format(((TradeRecord) record).getTradeDate()) +
                        "</Trade_date>\n" +
                        space + space +
                        "<Settled_date>" + format.format(((TradeRecord) record).getSettledDate()) +
                        "</Settled_date>\n" +
                        space + "</row>\n";
                break;
            }
            case 1: {
                result = space + "<row>\n" +
                        space + space +
                        "<Dividend_id>" + ((DividendRecord) record).getDivId() + "</Dividend_id>\n" +
                        space + space +
                        "<Trade_id>" + ((DividendRecord) record).getTradeId() + "</Trade_id>\n" +
                        space + space +
                        "<Acct>" + ((DividendRecord) record).getAcct() + "</Acct>\n" +
                        space + space +
                        "<Amount>" + ((DividendRecord) record).getAmount() + "</Amount>\n" +
                        space + "</row>\n";
                break;
            }
            case 2: {
                result = space + "<row>\n" +
                        space + space +
                        "<Claim_id>" + ((ClaimRecord) record).getClaimID() + "</Claim_id>\n" +
                        space + space +
                        "<Trade_id>" + ((ClaimRecord) record).getTradeID() + "</Trade_id>\n" +
                        space + space +
                        "<Rec_acct>" + ((ClaimRecord) record).getRecAcct() + "</Rec_acct>\n" +
                        space + space +
                        "<Ent_acct>" + ((ClaimRecord) record).getEntAcct() + "</Ent_acct>\n" +
                        space + space +
                        "<Amount>" + ((ClaimRecord) record).getAmount() + "</Amount>\n" +
                        space + "</row>\n";
            }
        }
        return result;
    }

    private void writeCSV() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(pack.getHeader() + "\n");
            for (Record record : pack.getDataList()) {
                writer.write(record.toString() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
