package com.lhb.dds.hdds;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.*;
import java.util.*;

public class TagInit {

    public static void main(String[] args) throws IOException {
        Gson gson = new Gson();
        Table table = ConnectionFactory.createConnection().getTable(TableName.valueOf("headlines:item_meta_table"));
        Scan scan =new Scan();
        scan.addColumn("sys".getBytes(),"tags".getBytes());
        final byte[] sys ="sys".getBytes();
        final byte[] tags ="tags".getBytes();
        int cnt =0;

        Map<String,Integer> count = new HashMap<>();
        Iterator<Result> rs =table.getScanner(scan).iterator();
        while(rs.hasNext()) {
            Result r = rs.next();
            if(r.isEmpty())
                continue;
            Map<String,Float> v= gson.fromJson(Bytes.toString(r.getValue(sys,tags)),new TypeToken<Map<String,Float>>(){}.getType());
            for(String k:v.keySet())
                count.put(k,count.getOrDefault(k,0)+1);
        }

        PrintWriter write =new PrintWriter(new FileOutputStream(new File("/home/hadoop/result/tags.txt"),true));
        for(Map.Entry kv:count.entrySet())
            write.println(kv.getKey()+"/004"+kv.getValue());
        write.flush();
        write.close();
    }
}
