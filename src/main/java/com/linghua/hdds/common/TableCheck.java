package com.linghua.hdds.common;

import com.rongji.cms.webservice.client.json.ArticleClient;
import com.rongji.cms.webservice.client.json.CmsClientFactory;
import com.rongji.cms.webservice.domain.WsArticleFilter;
import com.rongji.cms.webservice.domain.WsArticleSynData;
import com.rongji.cms.webservice.domain.WsPage;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Table;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TableCheck {

    public static void main(String[] args) throws IOException {
//        System.out.println(TableUtil.IdReverse("981988280999959"));
            TableCheck check=new TableCheck();
            check.checkCmsAndHbase(1);

    }

    public int checkCmsAndHbase(int lastDay) throws IOException {

        Table table = ConnectionFactory.createConnection().getTable(TableName.valueOf("headlines:item_meta_table"));
        List<String> toReStore = new ArrayList<>();
        String result = HttpClientResource.doSend("http://cms.work.net/wNewsRecommend.sp?act=search&site=190019&rows=1000&flimit=200",null,"get");
//

        JSONArray obj = new JSONObject(result).getJSONObject("result").getJSONArray("articleList");
        System.out.println(obj.length());
        for(int i=0;i<obj.length();i++){
            JSONObject ar = obj.getJSONObject(i);

            toReStore.add(ar.getJSONArray("id").getJSONObject(0).getString("value"));

            if(!ar.getJSONArray("title").getJSONObject(0).has("value")){
                System.out.println(i+":"+ar.getJSONArray("id").getJSONObject(0).getString("value"));
            }
        }
        boolean[] boolExit =table.existsAll(toReStore.stream().map(id -> {
            Get get =new Get(TableUtil.IdReverse(id).getBytes());
//            get.addColumn("p".getBytes(),"ft".getBytes());
            return get;
        }).collect(Collectors.toList()));

        System.out.println(boolExit.length+"----");
        int k = 0;
        for(int j = 0; j<boolExit.length;j++){
            if(!boolExit[j]){
                System.out.println(toReStore.get(j));
                k++;
            }
        }
        CmsClientFactory fac = new CmsClientFactory("http://cms.work.net", "00000002", "A7dCV37Ip96%86");
        ArticleClient client = fac.getArticleClient();
        WsArticleFilter filter = new WsArticleFilter();

        WsPage page = new WsPage();


        try {
            for(int j=0;j<boolExit.length;j++){
                WsArticleSynData.ArticleVo article = client.findArticleVos(filter, page).getList().get(0);
//                article.

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(k);
        return 0;
    }

}
