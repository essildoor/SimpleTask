package com.returnonintelligence.datatypes;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Represents incoming data file
 * Consists of:
 * 1) info from data header:
 * 1.1) Share name
 * 1.2) EX date
 * 1.3) REC date
 * 1.4) Dividend amount per share
 *
 * 2) list of TradeRecord objects
 */
public class TradePackage extends AbstractPackage {

    private final int PACK_TYPE = 0;
    private String header;
    private String shareName;
    private Date exDate;
    private Date recDate;
    private int dividendAmountPerShare;
    private List<TradeRecord> dataList;

    public TradePackage(String shareName, Date exDate, Date recDate,
                        int dividendAmountPerShare, List<TradeRecord> dataList) throws IOException {
        this.shareName = shareName;
        this.exDate = exDate;
        this.recDate = recDate;
        this.dividendAmountPerShare = dividendAmountPerShare;
        this.dataList = dataList;
        this.header = "Trade_id,Seller_id,Buyer_id,Amount,TD,SD";

        if (recDate.compareTo(exDate) < 0)
            throw new IOException("Incorrect dates");
    }

    public int getPACK_TYPE() {
        return PACK_TYPE;
    }

    public String getShareName() {
        return shareName;
    }

    public Date getExDate() {
        return exDate;
    }

    public Date getRecDate() {
        return recDate;
    }

    public int getDividendAmountPerShare() {
        return dividendAmountPerShare;
    }

    public List<TradeRecord> getDataList() {
        return dataList;
    }

    public String getHeader() {
        return header;
    }
}
