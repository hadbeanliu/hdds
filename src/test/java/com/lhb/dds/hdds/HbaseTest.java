package com.lhb.dds.hdds;

import com.linghua.hdds.common.ExetractorKeyword;
import com.linghua.hdds.store.Item;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class HbaseTest {


    public static void main(String[] args){

        try {
            List<String> badLine=new ArrayList<>();
            Table table = ConnectionFactory.createConnection().getTable(TableName.valueOf("govheadlines:item_meta_table"));
            Scan scan =new Scan();

            Filter filter =new SingleColumnValueFilter("p".getBytes(),"lb".getBytes(), CompareFilter.CompareOp.EQUAL,"互动留言".getBytes());
            scan.setFilter(filter);
//            scan.setStartRow("982897980998720".getBytes());
            Iterator<Result> rs = table.getScanner(scan).iterator();
            List<Put> putList=new ArrayList<>();
            while(rs.hasNext()){
                Result r =rs.next();

                String cnt = Bytes.toString(r.getValue("p".getBytes(),"cnt".getBytes()));
                String t = Bytes.toString(r.getValue("p".getBytes(),"t".getBytes()));
//                if(r.getValue("p".getBytes(),"t".getBytes())!=null)
                String location = Bytes.toString(r.getValue("p".getBytes(),"t".getBytes()));
                System.out.println(location);
                if(t ==null ||cnt == null){
                    badLine.add(Bytes.toString(r.getRow()));
                    continue;
                }
                Item item = new Item();
                item.setContent(cnt);
                item.setTitle(t);
                if(location!=null){
                Map<String,String> sys=new HashMap<>();
                sys.put("location",location);
                item.setSys(sys);
                }
                ExetractorKeyword.exetract(item);
                if(item.getSys()!=null&&!item.getSys().isEmpty()){
                    Put put =new Put(r.getRow());
                    for(String s:item.getSys().keySet()){
                        put.addColumn("sys".getBytes(),s.getBytes(),item.getSys().get(s).getBytes());
                    }
                    if(!put.isEmpty())
                        putList.add(put);
                    if(putList.size()>500) {
                        System.out.println("has deal it: id ="+new String(r.getRow()));
                        table.put(putList);
                        putList.clear();
                    }
                }
            }

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
