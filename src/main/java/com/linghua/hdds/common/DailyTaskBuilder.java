package com.linghua.hdds.common;

import com.linghua.hdds.store.User;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.*;

public class DailyTaskBuilder {

    public static long dayTimeStamp = 24*60*60*1000;
    public void reComputeUserCatalogPrefs(double lastDay){
        try {
            long now = System.currentTimeMillis();
            Table actTable = ConnectionFactory.createConnection().getTable(TableName.valueOf("headlines:user_act_item_table"));
            Scan scan =new Scan();
            scan.setTimeRange(now-(long)(dayTimeStamp*lastDay+1l),now);
            Iterator<Result> rs = actTable.getScanner(scan).iterator();
            Map<String,Put> putMap=new HashMap<>();
            Set<String> iidSet=new HashSet<>();
            Map<String,Set<String>> iidWithUids=new HashMap<>();
            while(rs.hasNext()){
                Result r=rs.next();
                String[] acts = Bytes.toString(r.getRow()).split("_");
                if(acts.length == 3){
                    String uid = TableUtil.idReverseAndBuild(acts[0]);
                    String actType = acts[1];
                    String iid = TableUtil.IdReverse(acts[2]);
                    Set<String> iusers=iidWithUids.getOrDefault(iid,new HashSet<String>());
                    iusers.add(uid);
                    iidWithUids.put(iid,iusers);
                }
            }
            actTable.close();
            List<Get> itemGet=new ArrayList<>();
            iidWithUids.keySet().forEach(k ->{
                Get get=new Get(k.getBytes());
                get.addColumn("f".getBytes(),"lbId".getBytes());
                get.addColumn("sys".getBytes(),"tag".getBytes());
                itemGet.add(get);
            });
            Table itemTable = ConnectionFactory.createConnection().getTable(TableName.valueOf("headlines:item_meta_table"));

            Result[] items = itemTable.get(itemGet);
            Map<String,Map<String,Integer>> userIncrementMap = new HashMap<>();
            for(Result r:items){
                String lbId = Bytes.toString(r.getValue("f".getBytes(),"lbId".getBytes()));
                String tags = Bytes.toString(r.getValue("sys".getBytes(),"tag".getBytes()));
                if(lbId==null)
                    continue;
                Set<String> uids = iidWithUids.get(Bytes.toString(r.getRow()));
                uids.forEach(id->{
                    Map<String,Integer> catalogWithNum = userIncrementMap.getOrDefault(id, new HashMap<>());
                    catalogWithNum.put(lbId,catalogWithNum.getOrDefault(lbId,0)+1);
                    userIncrementMap.put(id,catalogWithNum);
                });
            }
            itemTable.close();
            Table userTable = ConnectionFactory.createConnection().getTable(TableName.valueOf("headlines:user_msg_table"));
//
            for(String key:userIncrementMap.keySet()){
                System.out.print(key+":>>");
                Map<String,Integer> bhv =userIncrementMap.get(key);
                Increment increment=new Increment(key.getBytes());
                for(Map.Entry<String,Integer> kv:bhv.entrySet()){
                    System.out.print(kv.getKey()+":"+kv.getValue()+";");
                    increment.addColumn("bhv".getBytes(),kv.getKey().getBytes(),kv.getValue());
                }
                System.out.println();
                userTable.increment(increment);
            }
            userTable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void clearUnAdaptableRecord() throws IOException {
        Table actTable = ConnectionFactory.createConnection().getTable(TableName.valueOf("headlines:user_act_item_table"));
        Scan scan =new Scan();
        Iterator<Result> rs = actTable.getScanner(scan).iterator();
        List<Delete> dels=new ArrayList<>();
        while(rs.hasNext()){
            Result r=rs.next();
            String acts = Bytes.toString(r.getRow());
            if(acts.indexOf("undefined")!= -1){
                dels.add(new Delete(r.getRow()));
            }

        }
        actTable.delete(dels);
        actTable.close();
    }


    public static void main(String[] args) throws IOException {
        Table actTable = ConnectionFactory.createConnection().getTable(TableName.valueOf("headlines:user_msg_table"));
        Get get =new Get("0000000000000066".getBytes());
        get.addFamily("bhv".getBytes());
        Result r = actTable.get(get);
        for(Cell cell:r.rawCells()){
            System.out.println(new String(cell.getQualifier())+":"+Bytes.toLong(cell.getValue()));
        }

    }
}
