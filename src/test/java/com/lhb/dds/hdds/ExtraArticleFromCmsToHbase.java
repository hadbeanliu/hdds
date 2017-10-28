package com.lhb.dds.hdds;

import com.linghua.hdds.common.TableUtil;
import com.rongji.cms.webservice.client.json.ArticleClient;
import com.rongji.cms.webservice.client.json.CmsClientFactory;
import com.rongji.cms.webservice.domain.WsArticleFilter;
import com.rongji.cms.webservice.domain.WsArticleSynData;
import com.rongji.cms.webservice.domain.WsPage;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExtraArticleFromCmsToHbase {


    public static void main(String[] args) {


        CmsClientFactory fac = new CmsClientFactory("http://cms.work.net", "00000002", "A7dCV37Ip96%86");
        ArticleClient client = fac.getArticleClient();
        try {
            Table table = ConnectionFactory.createConnection().getTable(TableName.valueOf("govheadlines:item_meta_table"));

            WsArticleFilter filter2 = new WsArticleFilter();
            int cnt = 0;
            int success = 0;
            int hm = 30;
            WsPage page = new WsPage();
            page.setPageSize(hm);
            filter2.setSiteIds(new String[]{"190014", "190020", "190021"});
            List<Put> puts = new ArrayList<>();
            for (int i = 1; i < 50000; i++) {
                page.setCurrPage(i);
                try {
                    List<WsArticleSynData.ArticleVo> vos = client.findArticleVos(filter2, page).getList();
                    for (WsArticleSynData.ArticleVo vo : vos) {
                        cnt++;
                        if (vo.get("content") != null) {
                            Put put = new Put(TableUtil.IdReverse(vo.get("id")).getBytes());
                            put.addColumn("f".getBytes(), "html".getBytes(), vo.get("content").getBytes());
                            puts.add(put);
                        }
                    }
                    if (puts.size() > 500) {
                        table.put(puts);
                        success+=puts.size();
                        puts.clear();
                        System.out.println("成功存储文章数量:"+success);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            if (puts.size() > 0) {
                table.put(puts);
                success+=puts.size();
                puts.clear();
                System.out.println(cnt+"篇文章中,成功存储文章数量:"+success);
            }
            System.out.println("总共找到文章数量:"+cnt);

        }catch(IOException e){
           e.printStackTrace();

        }

    }
}
