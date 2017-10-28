package com.linghua.hdds.api.conf;

import com.linghua.hdds.api.db.HbaseDaoImpl;
import com.linghua.hdds.meta.ActionType;
import org.apache.hadoop.hbase.client.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class TemporaryRecorder implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(TemporaryRecorder.class);
//    private static final TemporaryRecorder recoreder =new TemporaryRecorder(3000);

    private long sleepTime=3000;
    private boolean flag = false;
    private int minCache = 500;
    private int minTimeSpan = 60*1000;
    private long now =System.currentTimeMillis();
    private static final String TABLE_NAME="headlines:user_act_item_table";

    @Autowired
    protected HbaseDaoImpl ht;

//    public static TemporaryRecorder getInstance(){
//        return recoreder;
//    }


    public TemporaryRecorder() {
        this(3000);
    }

    public TemporaryRecorder(long sleepTime) {
        this.sleepTime = sleepTime;
    }
    public void setMinCache(int minCache){
        this.minCache=minCache;
    }

    public List<String> meta =new ArrayList<>();
    public List<String> tmpMeta =new ArrayList<>();

    @Override
    public void run() {

        while(true){
            if(meta.size() > minCache||(System.currentTimeMillis()-now)>minTimeSpan&&meta.size()!=0){
                flag = true;
                LOG.info("批量导入日志数据到hbase:"+meta.size());

                try{
                 List<Put> puts =new ArrayList<>();
//                    Map<String,>
                    for(String key:meta){
                        Put put =new Put(key.getBytes());
                        put.addColumn("f".getBytes(),"m".getBytes(),"".getBytes());
                        puts.add(put);
                    }
                    ht.put(TABLE_NAME,puts);
                }catch(Exception e){
                    e.printStackTrace();
                }finally {
                }
                meta.clear();
                flag = false;
                now = System.currentTimeMillis();
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
            this.tmpMeta.add(key);
        }else {
            this.meta.add(key);
            if(!this.tmpMeta.isEmpty()){
                for(String k:this.tmpMeta){
                    this.meta.add(k);
                }
                this.tmpMeta.clear();
            }
        }
    }
}
