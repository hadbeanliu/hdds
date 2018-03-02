package com.linghua.hdds.preference.model;


import com.linghua.hdds.meta.ActionType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ScoreEvaluator {

    private static long startTime = 1443686400000l;  // system start time on 2015/10/01 16:00:00
    private static float y = 0.01f;
    //according to the user's act with this item ,caculate the score rank, base on the  s = log10'z + yt/45000
    public static float evaluate(Map<String,Long> act,long time){
//        ActionType.values()
//        float score = act.getOrDefault(Act)
        double mark =act.getOrDefault(ActionType.COLLECT.getName(),0l)*2 +
                act.getOrDefault(ActionType.VIEW.getName(),0l)*0.1 +
                 act.getOrDefault(ActionType.LIKE.getName(),0l) +
                  act.getOrDefault(ActionType.SHARE.getName(),0l)*3 ;

        return (float) (Math.log(mark + 1) + y*(time -  startTime)/45000000);
    }

    public static void main(String[] args){

        SimpleDateFormat format =new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        try {
            Date date=format.parse("20151001 16:00:00");
            System.out.println(date.getTime());
            long millS = new Date().getTime()-date.getTime();
            System.out.println(millS);
            System.out.println(millS/(24*60*60*1000));


            System.out.println(45000f/(24*60*60));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
