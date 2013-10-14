package com.returnonintelligence.datatypes;

import java.util.List;

/**
 * Represents dividend file data
 * Consists of
 * 1) file name
 * 2) List of DividendData objects
 */
public class DividendPackage extends AbstractPackage {

    private final int PACK_TYPE = 1;
    private String header;
    private List<DividendRecord> dataList;

    public int getPACK_TYPE() {
        return PACK_TYPE;
    }

    public DividendPackage(List<DividendRecord> dataList) {
        this.dataList = dataList;
        this.header = "Div_id,Trade_id,Acct,Amount";
    }

    public String getHeader() {
        return header;
    }

    public List<DividendRecord> getDataList() {
        return dataList;
    }
}
