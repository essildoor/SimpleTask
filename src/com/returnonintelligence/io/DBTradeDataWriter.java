package com.returnonintelligence.io;

import com.returnonintelligence.datatypes.TradePackage;
import com.returnonintelligence.datatypes.TradeRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DBTradeDataWriter {
    private TradePackage tradePackage;
    private String shareName;
    private Date exDate;
    private Date recDate;
    private int divAmntPerShare;
    private static String url = "jdbc:mysql://localhost:3306/";
    private static String user = "root";
    private static String password = "4d48aa17cf";

    public DBTradeDataWriter(TradePackage tradePackage) {
        this.tradePackage = tradePackage;
        this.exDate = tradePackage.getExDate();
        this.recDate = tradePackage.getRecDate();
        this.shareName = tradePackage.getShareName();
        this.divAmntPerShare = tradePackage.getDividendAmountPerShare();
    }

    /**
     * Constructs sql query string regarding tradeRecord data
     *
     * @param record TradeRecord instance
     * @return query string
     */
    private String makeQuery(TradeRecord record) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        StringBuilder builder = new StringBuilder(90);
        builder.append("INSERT INTO tradedb.tradedata VALUES (");
        builder.append("'");
        builder.append(Integer.parseInt(record.getTradeId()));
        builder.append("',");
        builder.append("'");
        builder.append(record.getBuyerAcct());
        builder.append("',");
        builder.append("'");
        builder.append(record.getSellerAcct());
        builder.append("',");
        builder.append("'");
        builder.append(record.getAmount());
        builder.append("',");
        builder.append("'");
        builder.append(format.format(record.getTradeDate()));
        builder.append("',");
        builder.append("'");
        builder.append(format.format(record.getSettledDate()));
        builder.append("',");
        builder.append("'");
        builder.append(shareName);
        builder.append("',");
        builder.append("'");
        builder.append(format.format(exDate));
        builder.append("',");
        builder.append("'");
        builder.append(format.format(recDate));
        builder.append("',");
        builder.append("'");
        builder.append(divAmntPerShare);
        builder.append("')");
        return new String(builder);
    }

    /**
     * Writes all records from TradePackage dataList as rows into MySQL DB
     */
    public void write() {
        String query = null;
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            for (TradeRecord record : tradePackage.getDataList()) {
                query = makeQuery(record);
                statement.executeUpdate(query);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
