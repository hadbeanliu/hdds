package com.lhb.dds.hdds;

import com.linghua.hdds.common.HtmlParser;
import com.linghua.hdds.common.MainWordExtractor;
import com.linghua.hdds.meta.TwoTuple;
import com.rongji.cms.webservice.client.json.ArticleClient;
import com.rongji.cms.webservice.client.json.CmsClientFactory;
import com.rongji.cms.webservice.domain.WsArticleFilter;
import com.rongji.cms.webservice.domain.WsArticleSynData;
import com.rongji.cms.webservice.domain.WsPage;

import java.util.ArrayList;
import java.util.List;

public class SimpleTest {

    public static void main(String[] args) throws Exception {
        CmsClientFactory fac = new CmsClientFactory("http://cms.work.net", "00000002", "A7dCV37Ip96%86");
        ArticleClient client = fac.getArticleClient();
        WsArticleFilter filter = new WsArticleFilter();

        filter.setArIds(new String[]{"2018011819001019"});

        WsPage page = new WsPage();
        WsArticleSynData.ArticleVo article = client.findArticleVos(filter, page).getList().get(0);
        String content = article.get("content");
        List<TwoTuple<String, String>> words = null;
//        MainWordExtractor extractor = MainWordExtractor.getInstance();
//        words = extractor.simpleTokenizeWithPart(HtmlParser.delHTMLTag(content));

        System.out.println("-----------------------");
        System.out.println(content);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println(HtmlParser.delHTMLTag(content));

//        System.out.println(words);
    }

}
