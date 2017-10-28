package com.lhb.dds.hdds;

import com.linghua.hdds.common.ExetractorKeyword;
import com.linghua.hdds.store.Item;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class GetXYFromGeode {




   public static void main(String[] args) {
            byte[] family ="sys".getBytes();
            byte[] q="xy".getBytes();
            int cnt =0;
        try {
            List<String> badLine=new ArrayList<>();
            Table table = ConnectionFactory.createConnection().getTable(TableName.valueOf("govheadlines:item_meta_table"));
            Scan scan =new Scan();

            scan.addColumn("sys".getBytes(),"place".getBytes());
            Filter filter =new SingleColumnValueFilter("p".getBytes(),"lb".getBytes(), CompareFilter.CompareOp.EQUAL,"互动留言".getBytes());
            scan.setFilter(filter);
            Iterator<Result> rs = table.getScanner(scan).iterator();
            List<Put> putList=new ArrayList<>();
            while(rs.hasNext()){
                Result r =rs.next();
                if(!r.isEmpty()){
                    String location = Bytes.toString(r.getValue("sys".getBytes(),"location".getBytes()));
                    String xy =ExetractorKeyword.getXY(location);
                    if(xy!=null){
                        Put put =new Put(r.getRow());
                        put.addColumn(family,q,xy.getBytes());
                        putList.add(put);
                        cnt++;
                    }
                    if(putList.size()>500){
                        table.put(putList);
                        putList.clear();
                    }
                }


            }
            table.put(putList);
            cnt+=putList.size();
            System.out.println("success get "+cnt+"---bad get"+ badLine.size());
            table.close();
            PrintWriter write =new PrintWriter(new FileOutputStream(new File("/home/hadoop/result/badLine.txt")));
            for(String s:badLine)
                write.println(s);
            write.flush();
            write.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
