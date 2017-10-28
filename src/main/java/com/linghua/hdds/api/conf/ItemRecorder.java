package com.linghua.hdds.api.conf;

import com.google.gson.Gson;
import com.linghua.hdds.api.db.HbaseDaoImpl;
import com.linghua.hdds.meta.ActionType;
import org.apache.hadoop.hbase.client.Increment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemRecorder implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ItemRecorder.class);
//    private static final ItemRecorder recoreder =new ItemRecorder(3000);

    private long sleepTime=3000;
    private boolean flag = false;
    private int minCache = 500;
    private int minTimeSpan = 60*1000;
    private long now =System.currentTimeMillis();
    private static final String TABLE_NAME="headlines:item_meta_table";

    @Autowired
    protected HbaseDaoImpl ht;

//    public static ItemRecorder getInstance(){
//        return recoreder;
//    }

    public ItemRecorder(){
        this(3000);
    }

    public ItemRecorder(long sleepTime) {
        this.sleepTime = sleepTime;
    }
    public void setMinCache(int minCache){
        this.minCache=minCache;
    }

    public Map<String,Map<String,Long>> meta =new HashMap<>();
    public Map<String,Map<String,Long>> tmpMeta =new HashMap<>();
  
    @Override
    public void run() {

        while(true){
            if(meta.size() > minCache||(System.currentTimeMillis()-now)>minTimeSpan&&meta.size()!=0){
                flag = true;
                LOG.info("批量导入日志数据到hbase:"+meta.size());
                try{

                    List<Increment> increments = new ArrayList<>();
                    for(String key:meta.keySet()){
                        Increment increment =new Increment(key.getBytes());
                        for(Map.Entry<String,Long> kv: this.meta.get(key).entrySet()){
                            System.out.println(kv);
                            increment.addColumn("his".getBytes(),kv.getKey().getBytes(),kv.getValue());
                        }
                        increments.add(increment);
                    }
                    ht.increments(TABLE_NAME,increments);
                }catch(Exception e){
                    e.printStackTrace();
                }finally {

                }
                meta.clear();
                flag = false;
                now =System.currentTimeMillis();
            }
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public synchronized void add(String key, ActionType type){
        if(flag){
           Map<String,Long> his = this.tmpMeta.getOrDefault(key,new HashMap<String,Long>());
           his.put(type.getName(),his.getOrDefault(type.getName(),0l)+type.getIndex());
           this.tmpMeta.put(key,his);
        }else {
            Map<String,Long> his = this.meta.getOrDefault(key,new HashMap<String,Long>());
            his.put(type.getName(),his.getOrDefault(type.getName(),0l)+type.getIndex());
            this.meta.put(key,his);
            if(!this.tmpMeta.isEmpty()){
                for(String tmpK:this.tmpMeta.keySet()){
                    Map<String,Long> his2 = this.meta.getOrDefault(tmpK,new HashMap<String,Long>());
                    for(Map.Entry<String,Long> kv: this.tmpMeta.get(tmpK).entrySet()){
                        his2.put(kv.getKey(),his2.getOrDefault(kv.getKey(),0l)+kv.getValue());
                    }
                    this.meta.put(key,his2);
                }
                this.tmpMeta.clear();
            }
        }
    }
}
