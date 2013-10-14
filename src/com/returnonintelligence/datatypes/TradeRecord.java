package com.returnonintelligence.datatypes;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Single record from Input file
 */
public class TradeRecord implements Record{

    private String tradeId;
    private String sellerAcct;
    private String buyerAcct;
    private int amount;
    private Date tradeDate;
    private Date settledDate;


    public TradeRecord(String tradeId, String sellerAcct, String buyerAcct, int amount,
                       Date tradeDate, Date settledDate) {
        this.tradeId = tradeId;
        this.sellerAcct = sellerAcct;
        this.buyerAcct = buyerAcct;
        this.amount = amount;
        this.tradeDate = tradeDate;
        this.settledDate = settledDate;
    }

    public String getTradeId() {
        return tradeId;
    }

    public String getSellerAcct() {
        return sellerAcct;
    }

    public String getBuyerAcct() {
        return buyerAcct;
    }

    public int getAmount() {
        return amount;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public Date getSettledDate() {
        return settledDate;
    }

    @Override
    public String toString() {
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
        return tradeId + "," + sellerAcct + "," + buyerAcct + "," + amount + "," +
                format.format(tradeDate) + "," + format.format(settledDate);
    }
}
