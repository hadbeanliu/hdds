package com.linghua.hdds.meta;

import org.apache.hadoop.hbase.client.Put;

public class ActionMeta {


    public static final Hcolumn DEFAULT_COL =new Hcolumn("f".getBytes(),"m".getBytes(), Hcolumn.Type.STRING);
    private String tableName;
    private String operator;
    private String key;
    private Hcolumn col;
    private long value;

    public ActionMeta(String tableName, String operator, String key, Hcolumn col, long value) {
        this.tableName = tableName;
        this.operator = operator;
        this.key = key;
        this.col = col;
        this.value = value;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Hcolumn getCol() {
        return col;
    }

    public void setCol(Hcolumn col) {
        this.col = col;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

}
