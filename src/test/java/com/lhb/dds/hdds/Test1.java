package com.lhb.dds.hdds;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.linghua.hdds.common.ExetractorKeyword;
import com.linghua.hdds.common.HttpClientResource;
import com.linghua.hdds.common.MainWordExtractor;
import com.linghua.hdds.meta.TwoTuple;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.lionsoul.jcseg.tokenizer.core.JcsegException;

import java.io.IOException;
import java.util.*;

public class Test1 {

    public static List<String> lines = new ArrayList<>();

    public static void main(String[] args) {
        try {
            Table table = ConnectionFactory.createConnection().getTable(TableName.valueOf("headlines:user_msg_table"));
            Scan scan =new Scan();
            Iterator<Result> rs = table.getScanner(scan).iterator();
            List<Delete> putList=new ArrayList<>();
            while (rs.hasNext()){
                Result r = rs.next();
                if(!new String(r.getRow()).equals("root")){
                    putList.add(new Delete(r.getRow()));
                }
            }
            table.delete(putList);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
class LineListener implements Runnable{

    private List<String> lines = new ArrayList<>();
    private static int cnt=0;
    @Override
    public void run() {
        while(true){
            if(lines.size() > 3){
                System.out.println("ouput to hbase:"+lines.size());

                lines.clear();
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

    public synchronized void add(String line){
        this.lines.add(line);
    }
}