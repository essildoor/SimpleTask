package com.returnonintelligence.datatypes;

/**
 * Single record from Output file
 */
public class ClaimRecord implements Record{
    private String claimID;
    private String tradeID;
    private String recAcct;
    private String entAcct;
    private final int amount;

    /**
     *
     * @param claimID - claim unique id, 10 digits
     * @param tradeID - trade unique id, 10 digits
     * @param recAcct - account which receive dividends, up to 10 digits
     * @param entAcct - account entitled to receive dividends, up to 10 digits
     * @param amount - amount of dividend to be transferred
     */
    public ClaimRecord(String claimID, String tradeID, String recAcct, String entAcct, int amount) {
        this.claimID = claimID;
        this.tradeID = tradeID;
        this.recAcct = recAcct;
        this.entAcct = entAcct;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return claimID + "," + tradeID + "," + recAcct + "," + entAcct + "," + amount;
    }

    public String getClaimID() {
        return claimID;
    }

    public String getTradeID() {
        return tradeID;
    }

    public String getRecAcct() {
        return recAcct;
    }

    public String getEntAcct() {
        return entAcct;
    }

    public int getAmount() {
        return amount;
    }
}
