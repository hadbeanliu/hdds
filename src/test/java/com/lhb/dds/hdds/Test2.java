package com.lhb.dds.hdds;

import com.google.gson.Gson;
import com.linghua.hdds.common.ExetractorKeyword;
import com.linghua.hdds.store.Item;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Test2 {

    public static void main(String[] args) throws IOException {
        Table table = ConnectionFactory.createConnection().getTable(TableName.valueOf("govheadlines:item_meta_table"));
        Scan scan =new Scan();
        scan.addColumn("p".getBytes(),"t".getBytes());
        scan.addColumn("p".getBytes(),"cnt".getBytes());
        int cnt =0;
        byte[] sys="sys".getBytes();
        byte[] tags="tags".getBytes();
        List<Put> puts =new ArrayList<>();
        Iterator<Result> rs =table.getScanner(scan).iterator();
        while(rs.hasNext()){
            Result r = rs.next();
            Item item =new Item();
            item.setTitle(Bytes.toString(r.getValue("p".getBytes(),"t".getBytes())));
            item.setContent(Bytes.toString(r.getValue("p".getBytes(),"cnt".getBytes())));
            ExetractorKeyword.exetract(item);
            if(item.getSys()==null)
                continue;
            if(item.getSys().get("tags")!=null){
                Put put = new Put(r.getRow());
                put.addColumn(sys,tags,item.getSys().get("tags").getBytes());
                puts.add(put);
            }
            if(puts.size()>500){
                table.put(puts);
                puts.clear();
                cnt += 500;
                System.out.println(Bytes.toString(r.getRow())+"/004"+cnt);
            }
        }
        table.put(puts);
        cnt += puts.size();
        puts.clear();
        System.out.println(cnt);

    }
}
