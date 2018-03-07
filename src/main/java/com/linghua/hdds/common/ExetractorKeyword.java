package com.linghua.hdds.common;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.linghua.hdds.meta.GeoMapResponse;
import com.linghua.hdds.meta.TwoTuple;
import com.linghua.hdds.store.Item;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ExetractorKeyword {


    private static Random r=new Random();

    public static void exetract(Item item,String bizCode){

        MainWordExtractor extractor = MainWordExtractor.getInstance();
        try {
            Map<String,String> sysSet=item.getSys() ==null ?new HashMap<>():item.getSys();
            List<TwoTuple<String,String>> words = extractor.simpleTokenizeWithPart(item.getContent());
            if(words == null)
                return ;
            List<String> toTrain =new ArrayList<>();
            Map<String,String> wordWityType = new HashMap<>();
            for(TwoTuple<String,String> w: words ){
                wordWityType.put(w._1,w._2);
                toTrain.add(w._1);
            }

            Map<String,String> keyform = extractor.tokenize(item.getTitle());
            String recString = HttpClientResource.post(gson.toJson(toTrain),
                    "http://slave2:9999/mining/getMainKey?biz_code="+"govheadlines" + "&ss_code=clsfy&hm=30");
            List<TwoTuple<String,Float>> result =gson.fromJson(recString,new TypeToken<List<TwoTuple<String,Float>>>(){}.getType());
            Map<String,Float> tags=new HashMap<>();
            double min = 6.5 - result.size()/10;
            String location=sysSet.get("location")==null? null:sysSet.get("location");
            Float placeValue=0f;
            boolean hasLocate =location==null;
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
                        if(hasLocate && speech.equals("ns")){
                            if(r._2>placeValue){
                                location =r._1;
                                placeValue=r._2;
                            }
                        }
                    }
                }
            }

            for(String speech: keyform.keySet()){
                String[] titleTags = keyform.get(speech).split(",");
                for(String t:titleTags){
                    if(t.length()>0&&tags.get(t)==null){
                        tags.put(t,0.5f);
                    }
                }
            }
            if(item.getKeyword()!=null){
                for(String key:item.getKeyword().keySet()){

                }
            }

            if(tags.size()>0){
                if(tags.size()>5){
                     List<Map.Entry<String,Float>> subList=new ArrayList<>(tags.entrySet()).stream().sorted((x,y)->y.getValue().compareTo(x.getValue())).collect(Collectors.toList()).subList(0,5);
                     tags.clear();
                     subList.stream().forEach(kv->tags.put(kv.getKey(),kv.getValue()));
                }
              sysSet.put("tags",gson.toJson(tags));
            }
            for(String k:keyform.keySet()) {
                if(k.equals("nt")&&sysSet.get("agency")==null)
                  sysSet.put("agency",keyform.get("nt"));
                else if(k.equals("nr"))
                  sysSet.put("figure",keyform.get("nr"));
                else if(k.equals("produce"))
                  sysSet.put("produce",keyform.get("np"));
            }

            if(location!=null){
                getPrecisFromBaiduMapWithrenderReverse(location,sysSet.get("city"),sysSet);
            }
            item.setSys(sysSet);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static boolean maybeErrorCatalog(String content,String bizCode,String catalog){
        String recString = HttpClientResource.post(content,
                "http://slave2:9999/mining/getMainKey?biz_code="+"govheadlines" + "&ss_code=clsfy&hm=30");




        return false;
    }

    private static Gson gson = new Gson();

    public static String getPrecisFromBaiduMapWithSearch(String place,String region,Map<String,String> sets){

        if (region ==null)
            region = "全国";
        String predics = getXYFromBaiduMap(place,region, sets);
        if(predics == null){
            return null;
        }

        String req = "http://api.map.baidu.com/place/v2/suggestion?city_limit=false&output=json&ak=4G9twNlyjRwRnvq3MOSGNoE6XSXZnGME&query="+place+"&region="+region;
        String res = HttpClientResource.post(null,req);
        JSONObject obj = new JSONObject(res);
        int status =obj.getInt("status");
        if(status!=0)
            return null;
        JSONArray list = obj.getJSONArray("result");
        if(list.length() == 0) {
            return null;
        }
        int index =-1;
        String[] lnglat1= predics.split(",");
        double lng1 = Double.valueOf(lnglat1[0]);
        double lat1 = Double.valueOf(lnglat1[1]);
        double min = Double.MAX_VALUE;
        for(int i=0;i<list.length();i++){
            JSONObject result = list.getJSONObject(i);
            if(!result.has("location"))
                continue;
            JSONObject location = result.getJSONObject("location");
            double lng2 = location.getDouble("lng");
            double lat2 = location.getDouble("lat");
            double d =getDistanceFromMap(lng1,lat1,lng2,lat2);
            if(d<0.5&&d<min){
                min = d;
                index = i;
            }
        }
        if(index == -1)
            return null;

        JSONObject result = list.getJSONObject(index);
        String city2 = result.getString("city");
        String district = result.getString("district");
        String name = place;
        if(district.equals("")||district.equals(city2))
            return null;
        if(name.indexOf(city2)!=-1){
            sets.put("place",name);
        }else if(name.indexOf(district)!=-1){
            sets.put("place",city2+name);
        }else{
            sets.put("place",city2+district+name);
        }
        JSONObject location = result.getJSONObject("location");
        return location.getDouble("lng")+","+location.getDouble("lat");

    }
    public static String getPrecisFromBaiduMapWithrenderReverse(String place,String region,Map<String,String> sets){
        if (region ==null)
            region = "全国";
        String predics = getXYFromBaiduMap(place,region, sets);
        if(predics == null){
            return null;
        }

        String req = "http://api.map.baidu.com/geocoder/v2/?output=json&pois=0&ak=4G9twNlyjRwRnvq3MOSGNoE6XSXZnGME&location="+predics;
        String res = HttpClientResource.post(null,req);
        JSONObject obj = new JSONObject(res);
        int status =obj.getInt("status");
        if(status!=0)
            return null;
        JSONObject result = obj.getJSONObject("result");
        if(result.has("addressComponent")){
            JSONObject address = result.getJSONObject("addressComponent");
            String province = address.getString("province");
            String city = address.getString("city");
            String district = address.getString("district");
            if(place.indexOf(province)!=-1){
                sets.put("place",place);
            }else if(place.indexOf(city)!=-1){
                sets.put("place",province+place);
            }else if(place.indexOf(district)!=-1){
                sets.put("place",province+city+place);
            }else {
                sets.put("place",province+city+district+place);
            }

        }

        return sets.get("xy");




    }

    public static String getXYFromBaiduMap(String place,String city,Map<String,String> sets){
        String req = "http://api.map.baidu.com/geocoder/v2/?output=json&ak=4G9twNlyjRwRnvq3MOSGNoE6XSXZnGME&address="+place+"&city="+city;
        String res = HttpClientResource.post(null,req);
        JSONObject obj =new JSONObject(res);
        int status =obj.getInt("status");
        if(status!=0)
            return null;
        JSONObject result = obj.getJSONObject("result");
        if(result.getInt("confidence")<31)
            return null;
        JSONObject location = result.getJSONObject("location");
        double x=location.getDouble("lng");
        double y= location.getDouble("lat");
        sets.put("xy",x+","+y);
        return y+","+x;
    }
    public static String getXY(String place){

        try{
        GeoMapResponse geomap = gson.fromJson(HttpClientResource.post(null,
                "http://restapi.amap.com/v3/geocode/geo?address="+place+"&output=json&key=4G9twNlyjRwRnvq3MOSGNoE6XSXZnGME"),GeoMapResponse.class);
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

        String json = "{name:jack,age:22}";
        JSONObject obj=new JSONObject(json);
        System.out.println(obj.has("abv"));

    }

    public static double getDistanceFromMap(double lng1,double lat1,double lng2,double lat2){

        double d =0;
        lat1 =rad(lat1);
        lat2 = rad(lat2);
        double dlng = rad(lng1-lng2);
        double dlat = lat1-lat2;
        double sdlng = Math.sin(dlng/2.0);
        double sdlat = Math.sin(dlat/2.0);

        return 2* 6378.137
                * Math.asin(Math.sqrt(sdlat * sdlat + Math.cos(lat1)
                * Math.cos(lat2) * sdlng * sdlng));
    }
    private static double rad(double d){
        return d*Math.PI/180.0;
    }
}
