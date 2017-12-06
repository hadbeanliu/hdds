package com.linghua.hdds.api.resource;

import com.google.gson.Gson;
import com.linghua.hdds.api.response.Node;
import com.linghua.hdds.api.service.ItemService;
import com.linghua.hdds.common.*;
import com.linghua.hdds.store.Item;

import org.lionsoul.jcseg.tokenizer.core.JcsegException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/test")
public class TestController {


    @Autowired
    private ItemService itemService;

    @RequestMapping("/get_1/{biz}/{iid}")
    @ResponseBody
    public String getWithEx1(@PathVariable(value = "biz") String biz, @PathVariable(value = "iid") String iid) {

        Assert.notNull(iid, "item id must be required!");
        Assert.notNull(biz, "biz_code must be required!");
        Item item = itemService.get(biz, TableUtil.IdReverse(iid));
        return new Gson().toJson(item);
    }

    @RequestMapping("/get_2/{biz}/{iid}")
    @ResponseBody
    public String getWithEx2(@PathVariable(value = "biz") String biz, @PathVariable(value = "iid") String iid) {

        Assert.notNull(iid, "item id must be required!");
        Assert.notNull(biz, "biz_code must be required!");
        Item item = itemService.get(biz, TableUtil.IdReverse(iid));
        MainWordExtractor extractor = MainWordExtractor.getInstance();
        if(item.getSys()!=null){
            item.getSys().remove("place");
        }
        ExetractorKeyword.exetract(item);

        return new Gson().toJson(item);
    }
    @RequestMapping("/get/{biz}/{iid}")
    @ResponseBody
    public String get(@PathVariable(value = "biz") String biz, @PathVariable(value = "iid") String iid) {

        Assert.notNull(iid, "item id must be required!");
        Assert.notNull(biz, "biz_code must be required!");
        Item item = itemService.get(biz, TableUtil.IdReverse(iid));

        return new Gson().toJson(item);
    }
    @RequestMapping("/getbaxy/{address}/{city}")
    @ResponseBody
    public String getXYFromBadu(@PathVariable(value = "address") String address, @PathVariable(value = "city") String city) {

        String req = "http://api.map.baidu.com/geocoder/v2/?output=json&ak=4G9twNlyjRwRnvq3MOSGNoE6XSXZnGME&address="+address+"&city="+city;
        System.out.println(req);
        String res = HttpClientResource.post(null,req);

        return res;
    }


    @RequestMapping("/caId/catalog/{biz}/{site}")
    public String getCaNameAndId(@PathVariable String biz,@PathVariable String site){
        List<String> ss=new ArrayList<>();
        for(Node node :CataLogManager.getCatalogTree(site)){
            if(node.hasChldren()){
                for(Node node2 :node.getChildren()){
                    ss.add("update CMS_ARTICLE set SITE_ID='190020' WHERE CA_ID = '"+node2.getCaId()+"';");
                }
            }
            ss.add("update CMS_ARTICLE set SITE_ID='190020' WHERE CA_ID = '"+node.getCaId()+"';");
        }

        return new Gson().toJson(ss);
    }
    @RequestMapping("/catalog/{biz}/{site}")
    public String getAsss(@PathVariable String biz,@PathVariable String site){

        return new Gson().toJson(CataLogManager.getCatalogTree());
    }
}
