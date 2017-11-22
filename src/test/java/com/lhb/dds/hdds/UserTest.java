package com.lhb.dds.hdds;


import com.linghua.hdds.store.User;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;

import java.io.IOException;

public class UserTest {


    public static void main(String[] args){

        try {
            User u=new User();
            u.getMapping();
            Connection conn = ConnectionFactory.createConnection();
            Table table=conn.getTable(TableName.valueOf("headlines:user_msg_table"));
            Put put = new Put("0000000000000062".getBytes());
            put.addColumn("f".getBytes(),"name".getBytes(),"gkx".getBytes());
            put.addColumn("f".getBytes(),"type".getBytes(),"admin".getBytes());
            put.addColumn("f".getBytes(),"pid".getBytes(),"root".getBytes());
//            Put put = new Put("root".getBytes());
//            put.addColumn("f".getBytes(),"name".getBytes(),"系统超级管理员".getBytes());
//            put.addColumn("f".getBytes(),"type".getBytes(),"topper".getBytes());
//            put.addColumn("f".getBytes(),"pid".getBytes(),"root".getBytes());
//            String config=new Gson().toJson(CataLogManager.getCatalogTree("all"));
//            put.addColumn("f".getBytes(),"config".getBytes(),config.getBytes());
            table.put(put);
//            Delete del=new Delete("liuhb".getBytes());
//            table.delete(del);
            table.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
