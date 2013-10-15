package com.returnonintelligence.io;

import com.returnonintelligence.datatypes.TradePackage;
import com.returnonintelligence.datatypes.TradeRecord;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class XMLTradeFileReader implements Reader {

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
    public XMLTradeFileReader(String fileName) {
        this.fileName = fileName;
        tradeRecordsList = new ArrayList<TradeRecord>();
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

    private void readData() {
        TradeRecord rec;
        File fXmlFile = new File(fileName);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;

        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            NodeList nList = doc.getElementsByTagName("row");
            String name;
            String value;
            int fileLinesNumber = nList.getLength();

            String tradeId = null;
            String sellerAcct = null;
            String buyerAcct = null;
            int amount = 0;
            Date tradeDate = null;
            Date settledDate = null;

            SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");

            //iterates trough whole file
            for (int i = 0; i < fileLinesNumber; i++) {
                NodeList nnList = nList.item(i).getChildNodes();
                if (nnList != null) {
                    //iterates trough one row
                    for (int j = 0; j < nnList.getLength(); j++) {
                        Node n = nnList.item(j);
                        if (n.getNodeType() == Node.ELEMENT_NODE) {
                            name = n.getNodeName();
                            value = n.getTextContent();
                            //reads data to temp lists according fields names
                            if (name.equals("Trade_id")) {
                                tradeId = value;
                                continue;
                            }
                            if (name.equals("Seller_acct")) {
                                sellerAcct = value;
                                continue;
                            }
                            if (name.equals("Buyer_acct")) {
                                buyerAcct = value;
                                continue;
                            }
                            if (name.equals("Amount")) {
                                amount = Integer.parseInt(value);
                                continue;
                            }
                            if (name.equals("Trade_date")) {
                                try {
                                    tradeDate = format.parse(value);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                continue;
                            }
                            if (name.equals("Settled_date")) {
                                try {
                                    settledDate = format.parse(value);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }
                    //stores obtained data from fields to TradeRecord instance
                    rec = new TradeRecord(tradeId, sellerAcct, buyerAcct, amount,
                            tradeDate, settledDate);
                    tradeRecordsList.add(rec);
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
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
}
