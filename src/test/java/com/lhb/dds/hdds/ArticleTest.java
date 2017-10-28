package com.lhb.dds.hdds;

import com.linghua.hdds.common.ExetractorKeyword;
import com.linghua.hdds.common.HtmlParser;
import com.linghua.hdds.common.MainWordExtractor;
import com.linghua.hdds.common.TableUtil;
import com.linghua.hdds.meta.TwoTuple;
import com.linghua.hdds.store.Item;
import com.rongji.cms.webservice.client.json.ArticleClient;
import com.rongji.cms.webservice.client.json.CmsClientFactory;
import com.rongji.cms.webservice.domain.*;
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

public class ArticleTest {

    public static void main(String[] args){

        CmsClientFactory fac=new CmsClientFactory("http://cms.work.net","00000002","A7dCV37Ip96%86");
        ArticleClient client= fac.getArticleClient();
        try {
            List<String> badLine=new ArrayList<>();
            Table table = ConnectionFactory.createConnection().getTable(TableName.valueOf("govheadlines:item_meta_table"));
            Scan scan =new Scan();
            scan.addColumn("sys".getBytes(),"xy".getBytes());
            scan.addColumn("sys".getBytes(),"place".getBytes());
            scan.addColumn("sys".getBytes(),"location".getBytes());
            Filter filter =new SingleColumnValueFilter("p".getBytes(),"lb".getBytes(), CompareFilter.CompareOp.EQUAL,"互动留言".getBytes());
            scan.setFilter(filter);
//            scan.setStartRow("982897980998720".getBytes());
            int hm =50;
            Iterator<Result> rs = table.getScanner(scan).iterator();
            Map<String,TwoTuple<String,String>> putList=new HashMap<>();
            String[] ids= new String[hm];
            WsArticleFilter filter2 = new WsArticleFilter();
            int cnt =0;
            int success=0;
            WsPage page = new WsPage();
            page.setPageSize(hm);
            while(rs.hasNext()) {
                Result r = rs.next();

                String xy = Bytes.toString(r.getValue("sys".getBytes(), "xy".getBytes()));
                String location = null;
                if(r.getValue("sys".getBytes(), "location".getBytes())!=null){
                    location=Bytes.toString(r.getValue("sys".getBytes(), "location".getBytes()));

                }else {
                    location=Bytes.toString(r.getValue("sys".getBytes(), "place".getBytes()));

                }
                if (location == null || xy == null) {
                    cnt ++;
                    badLine.add(Bytes.toString(r.getRow()));
                    continue;
                }
                putList.put(TableUtil.IdReverse(Bytes.toString(r.getRow())), new TwoTuple<String, String>(location, xy));
                if (putList.size() == hm) {
                    putList.keySet().toArray(ids);
                    filter2.setArIds(ids);
                    try {
                        List<WsArticleSynData.ArticleVo> vos = client.findArticleVos(filter2, page).getList();
                        for (WsArticleSynData.ArticleVo vo : vos) {
//                            System.out.println(vo.getVoId());
                            TwoTuple<String, String> tuple = putList.get(vo.get("id"));
                            if(tuple ==null){
                                cnt++;
                                continue;
                            }
                            vo.put("location", tuple._1);
                            String[] x_y = tuple._2.split(",");
                            vo.put("latalng", x_y[1] + "," + x_y[0]);
                            client.saveArticleSynData(vo.get("caId"), vo);
                            success++;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        badLine.addAll(Arrays.asList(ids));
                    }
                    System.out.println("cnt ==="+cnt+"---"+badLine.size()+"--success=="+success);
                    putList.clear();
                    ids = new String[hm];
                }
            }
            System.out.println("------"+cnt);
                    putList.keySet().toArray(ids);
                    filter2.setArIds(ids);

                List<WsArticleSynData.ArticleVo> vos = client.findArticleVos(filter2, page).getList();
                for (WsArticleSynData.ArticleVo vo : vos) {

                    TwoTuple<String, String> tuple = putList.get(vo.getVoId());
                    if(tuple==null){
                        cnt++;
                        continue;
                    }
                    System.out.println(tuple);
                    vo.put("location", tuple._1);
                    String[] x_y = tuple._2.split(",");
                    vo.put("latalng", x_y[1] + "," + x_y[0]);
                    client.saveArticleSynData(vo.get("caId"), vo);
                }
            PrintWriter write =new PrintWriter(new FileOutputStream(new File("/home/hadoop/result/badLine2.txt")));
            for(String s:badLine)
                write.println(s);
            write.flush();
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
