package com.returnonintelligence.datatypes;

import java.util.List;

public class ClaimPackage extends AbstractPackage {

    private final int PACK_TYPE = 2;
    private String header;
    private List<ClaimRecord> dataList;

    public ClaimPackage(List<ClaimRecord> dataList) {
        this.dataList = dataList;
        this.header = "Claim_id,Trade_id,Rec_acct,Ent_acct,Amount";
    }

    public int getPACK_TYPE() {
        return PACK_TYPE;
    }

    @Override
    public String getHeader() {
        return header;
    }

    @Override
    public List<ClaimRecord> getDataList() {
        return dataList;
    }
}
