package com.returnonintelligence.datatypes;

import java.util.List;

public abstract class AbstractPackage {

    /*protected String header;
    protected List<? extends Record> dataList;*/

    public abstract String getHeader();
    public abstract List<? extends Record> getDataList();
    public abstract int getPACK_TYPE();
}
