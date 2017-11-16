package com.lhb.dds.hdds;

import com.linghua.hdds.common.CataLogManager;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Test3 {

    public static void main(String[] args) throws IOException {
        Table table = ConnectionFactory.createConnection().getTable(TableName.valueOf("govheadlines:item_meta_table"));
        Scan scan =new Scan();
        scan.addColumn("f".getBytes(),"lb".getBytes());
        scan.addColumn("f".getBytes(),"lbId".getBytes());
        String[] sites = {"headlines","190020","190021"};
        int cnt =0;
        byte[] sys="sys".getBytes();
        byte[] tags="tags".getBytes();
        List<Put> puts =new ArrayList<>();
        Iterator<Result> rs =table.getScanner(scan).iterator();
        while(rs.hasNext()){
            Result r = rs.next();
            if(r.getValue("f".getBytes(),"lbId".getBytes())==null){
                String lbid = null;
                String lb = Bytes.toString(r.getValue("f".getBytes(),"lb".getBytes()));
                for(String site:sites) {
                    lbid = CataLogManager.findCaIdByName(site, lb);
                    if(lbid!=null){

                        Put put =new Put(r.getRow());
                        put.addColumn("f".getBytes(),"lbId".getBytes(),lbid.getBytes());
                        puts.add(put);
                        if(puts.size()>500){
                            table.put(puts);
                            cnt +=puts.size();
                            puts.clear();
                        }
                        break;
                    }
                }
            }
        }
        table.put(puts);
        cnt +=puts.size();
        System.out.println("cnt="+cnt);
    }
}
