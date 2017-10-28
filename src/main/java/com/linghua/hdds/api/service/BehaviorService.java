package com.linghua.hdds.api.service;

import com.linghua.hdds.meta.Hcolumn;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Service;

@Service("bhvService")
public class BehaviorService extends ServiceTemplate{

    private static final String tableName="user_act_item_table";


    @Override
    protected String getTablename(String bizCode) {
        return bizCode +":"+ tableName ;
    }


    public boolean exist(String biz,String row ,String family,String qualify){
        return ht.exist(getTablename(biz),row,family,qualify);
    }

    public void put(String biz, String row, Hcolumn col,Long value){
//        ht.put(getTablename(biz),row,new String);
//        ht.put(getTablename(biz),row,new String);
        Put put =new Put(row.getBytes());
        put.addColumn(col.getFamily(),col.getQualifier(), Bytes.toBytes(value));
        ht.put(getTablename(biz),put);

    }

    @Override
    RowMapper getMapper() {
        return null;
    }
}
