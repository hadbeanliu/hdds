package com.linghua.hdds.api.conf;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.linghua.hdds.api.db.HbaseDaoImpl;
import com.linghua.hdds.common.TableUtil;
import com.linghua.hdds.meta.TwoTuple;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class UserGraphAnalysis {

    private static Logger log = LoggerFactory.getLogger(UserGraphAnalysis.class);

    private int cycleTime = 1;
    private static final String ACT_TABLE_NAME ="headlines:user_act_item_table";
//    private static UserGraphAnalysis userGraphAnalysis =new UserGraphAnalysis();
//    @Autowired
//    protected HbaseDaoImpl ht;

//    public static UserGraphAnalysis getInstance(){
//        return userGraphAnalysis;
//    }

    public UserGraphAnalysis(){

    }

    public void build(String biz){
        long end = System.currentTimeMillis();
        long start = end-60*24*60*1000*cycleTime;
        build(start,end,biz);
    }

    public void build(long minStamp, long maxStamp,String biz){
        log.info("<"+biz+">开始构建用花画像，from "+maxStamp +" to "+maxStamp);
        Connection conn=null;
        Table table=null;


        try {
            conn= ConnectionFactory.createConnection();
            Gson gson =new Gson();
            table=conn.getTable(TableName.valueOf(ACT_TABLE_NAME));


            Scan scan=new Scan();
//            scan.setStopRow(stopRow.getBytes());
            scan.setTimeRange(minStamp,maxStamp);
            final byte[] f= "f".getBytes();
            final byte[] q ="m".getBytes();
            scan.addColumn(f,q);

            ResultScanner rs=table.getScanner(scan);
            Iterator<Result> rit=rs.iterator();
            Map<String,List<TwoTuple<String,String>>> itemWithUser = new HashMap<>();
            Map<String,Map<String,Double>> userWithTags = new HashMap<>();
            while(rit.hasNext()){
                Result r = rit.next();
                String[] userAndItem = Bytes.toString(r.getRow()).split("_");
                if(userAndItem.length < 3)
                    continue;
                String iid = TableUtil.IdReverse(userAndItem[2]);

                List<TwoTuple<String,String>> acts = itemWithUser.getOrDefault(iid,new ArrayList<>());
                acts.add(new TwoTuple(userAndItem[0],userAndItem[1]));
                itemWithUser.put(iid, acts);

            }
            byte[] sys ="sys".getBytes();
            byte[] tags ="tags".getBytes();
            List<Get> items = itemWithUser.keySet().stream().map(id -> {
                Get get =new Get(id.getBytes());
                get.addColumn("sys".getBytes(),"tags".getBytes());
                return get;
            }).collect(Collectors.toList());
            Table itemTable = conn.getTable(TableName.valueOf("headlines:item_meta_table"));
            boolean[] exits = itemTable.existsAll(items);
            List<Get> exitOnHeadlines = new ArrayList<>();
            for(int i =0;i<items.size();i++){
                if(exits[i])
                    exitOnHeadlines.add(items.get(i));
            }
            Result[] result1 = itemTable.get(exitOnHeadlines);
            for(Result res:result1){
                Map<String,Double> ts = gson.fromJson(Bytes.toString(res.getValue(sys,tags)),new TypeToken<Map<String,Double>>(){}.getType());
                for(TwoTuple<String,String> uact:itemWithUser.get(Bytes.toString(res.getRow()))){
                    Map<String,Double> graph = userWithTags.getOrDefault(uact._1,new HashMap<>());
                    int code = uact._2.hashCode();
                    switch (code){
                        case 118:executor(1.0,ts,graph);break;
                        case 98603:executor(5.0,ts,graph);break;
                        case 3455:executor(2.5,ts,graph);break;
                        case 3669:executor(3.0,ts,graph);break;
                        default:break;
                    }
                    userWithTags.put(uact._1,graph);
                }
            }
            itemTable.close();

            final List<Increment> increments =new ArrayList<>();
            for(String uid:userWithTags.keySet()){
                Increment inc=new Increment(uid.getBytes());
                userWithTags.get(uid).forEach((k,v)->{
                    inc.addColumn("g".getBytes(),k.getBytes(), (long) Math.floor(v));
                });
                increments.add(inc);
            }

//            System.out.println("ht="+ht);
            Table userTable = conn.getTable(TableName.valueOf("headlines:user_msg_table"));
            for(Increment increment:increments)
              userTable.increment(increment);
            userTable.close();
            log.info("构建完成，共build了共："+increments.size()+"个用户画像!!");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                table.close();
                conn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
    private Map<String,Double> executor(Double alpha,Map<String,Double> from,Map<String,Double> source){
        from.forEach((k,v) -> source.put(k,source.getOrDefault(k,0.0)+alpha*v));
        return from;
    }
    public void rebuild(String uid,int lastDay){

//        for()
    }

    public static void main(String[] args){
        UserGraphAnalysis userGraphAnalysis=new UserGraphAnalysis();
        userGraphAnalysis.build("headlines");
    }
}
