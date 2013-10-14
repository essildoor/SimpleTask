package com.returnonintelligence.datatypes;

/**
 * Single record from dividend file
 */
public class DividendRecord implements Record{

    private String divId;
    private String tradeId;
    private String acct;
    private final int amount;

    /**
     *
     * @param divId - generated unique id, 10 digits
     * @param tradeId - trade unique id
     * @param acct - account which received dividends
     * @param amount - amount of dividends to be transferred
     */
    public DividendRecord(String divId, String tradeId, String acct, int amount) {
        this.divId = divId;
        this.tradeId = tradeId;
        this.acct = acct;
        this.amount = amount;
    }

    public String getDivId() {
        return divId;
    }

    public String getTradeId() {
        return tradeId;
    }

    public String getAcct() {
        return acct;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return divId + "," + tradeId + "," + acct + "," + amount;
    }
}
