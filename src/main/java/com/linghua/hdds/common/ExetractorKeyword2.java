package com.linghua.hdds.common;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.linghua.hdds.meta.GeoMapResponse;
import com.linghua.hdds.meta.TwoTuple;
import com.linghua.hdds.store.Item;
import org.lionsoul.jcseg.tokenizer.core.JcsegException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExetractorKeyword2 {


    public static void exetract(Item item){

        MainWordExtractor extractor = MainWordExtractor.getInstance();
        try {
            Map<String,String> sysSet=item.getSys() ==null ?new HashMap<>():item.getSys();
            List<TwoTuple<String,String>> words = extractor.simpleTokenizeWithPart(item.getContent());
            List<String> toTrain =new ArrayList<>();
            Map<String,String> wordWityType = new HashMap<>();
            for(TwoTuple<String,String> w: words ){
                wordWityType.put(w._1,w._2);
                toTrain.add(w._1);
            }

            Map<String,String> keyform = extractor.tokenize(item.getTitle());
            String recString = HttpClientResource.post(gson.toJson(toTrain),
                    "http://slave2:9999/mining/getMainKey?biz_code=headlines" + "&ss_code=user-analys&hm=30");
            List<TwoTuple<String,Float>> result =gson.fromJson(recString,new TypeToken<List<TwoTuple<String,Float>>>(){}.getType());
            Map<String,Float> tags=new HashMap<>();
            double min = 6.5 - result.size()/10;
            String location=sysSet.get("location")==null? null:sysSet.get("location");
            Float placeValue=0f;
            for(TwoTuple<String,Float> r:result){
                String speech = wordWityType.get(r._1);
                if(speech!=null&&speech.startsWith("n")) {
                    if (r._2 > min) {
                        tags.put(r._1, r._2);
                    }
                    if(speech.length()>1&&!speech.equals("nz")){
                        String v = keyform.getOrDefault(speech,"");
                        if(v.indexOf(r._1) == -1){
                            v = v+","+r._1;
                            keyform.put(speech,v);
                        }
                        if(location==null && speech.equals("ns")){
                            if(r._2>placeValue){
                                location =r._1;
                                placeValue=r._2;
                            }
                        }
                    }
                }
            }
            if(tags.size()>0)
              sysSet.put("tags",gson.toJson(tags));
            sysSet.putAll(keyform);
            System.out.println("~~~~~"+location);

            if(location!=null){
                String xy=getXY(location);
                System.out.println("~~~~~"+location+"--"+xy);
                if(xy!=null){
                 sysSet.put("xy",getXY(location));
                 sysSet.put("place",location);
                }
            }
            item.setSys(sysSet);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JcsegException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private static Gson gson = new Gson();
    public static String getXY(String place){

        try{
        GeoMapResponse geomap = gson.fromJson(HttpClientResource.post(null,
                "http://restapi.amap.com/v3/geocode/geo?address="+place+"&city=福州&output=json&key=714c13cae8b905bd291fe6b7645d8b0e"),GeoMapResponse.class);
        if(geomap.getStatus()==1&&geomap.getCount()>0) {
            return geomap.getLocation();

        }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static String getYX(String place){
        String xy =getXY(place);
        if(xy!=null){
            return xy.split(",")[1]+xy.split(",")[0];
        }
        return null;
    }

    public static void main(String[] args){
        Item item =new Item();
        item.setTitle("芝加哥成全美鼠患重灾区 市府欲拨灭鼠专款");
        item.setContent("中新网10月19日电 美国《世界日报》刊文称，芝加哥已经连续第三年被美国知名驱虫公司orkin评为“全美老鼠之都”（rat capital），根据芝城市府报告，2017年至今，市府共接到3.9万通鼠患投诉电话，比2016年增长了三成，芝加哥市长伊曼纽计划在2018年拨出额外的150万元（美元，下同）经费，作为“消灭老鼠”专款。\n" +
                "\n" +
                "资料图：老鼠资料图：老鼠\n" +
                "据orkin公司对全美各地2016年9月至2017年9月居民住宅及商业灭鼠需求量统计，芝加哥地区连续第三年“蝉联”全美鼠患最严重的城市，洛杉矶和华府紧随其后，纽约则排行第四名。\n" +
                "\n" +
                "芝加哥市府对此解释，该市老鼠数量的暴增，是因为连续三年的暖冬天气，以及建筑项目繁多导致，这些建筑破坏了老鼠的地下巢穴，因此它们才会四下逃窜，涌入街头。\n" +
                "\n" +
                "伊曼纽在2018年的预算计划中增列了150万元作为控制鼠患专用经费，计划增加五名工作人员，负责消灭芝城街头及小巷中流窜的小动物。市府街道卫生清洁部门指出，2016年春天全市仅有八名鼠患控制人员，而新的计划一旦实施，到2018年全市的控鼠人员将达到30人。\n" +
                "\n" +
                "此外，伊曼纽还要求市议会拨款50万元，购买一万个黑色附轮子的新垃圾筒，以替换那些被老鼠啃过的旧筒。\n" +
                "\n" +
                "芝加哥街道及卫生清洁局发言人玛甘（sarah mcgann）表示，2017年至今，市府相关单位共接到3.9万个鼠患电话，这些申诉都在五天内获得处理。她提醒市民，驱除老鼠的最佳方法就是将放在巷子及院子里的垃圾盖好、封好。");
        ExetractorKeyword2 e=new ExetractorKeyword2();
        e.exetract(item);
    }


}
