package com.linghua.hdds.preference.model;

import com.google.common.collect.Maps;
import com.linghua.hdds.meta.TwoTuple;

import java.util.*;

public class TopItemRecommender {

    private Map<String,List<TwoTuple<String,Float>>> ranks = Maps.newConcurrentMap();
    private List<TwoTuple<String,Float>> allRanks = new ArrayList<>();
    private float min = -1;

    public void add(String lable,String id ,float score){

        if (score > min) {
            allRanks.add(new TwoTuple<>(id,score));
        }
    }

    public void sort(){
        allRanks.sort((x,y) -> y._2.compareTo(x._2));
    }
    public void desc(){

        System.out.println("a good idea:["+allRanks.size()+"]");
    }

    public List<TwoTuple<String,Float>> weekRank(int num){
        return null;
    }

    public List<TwoTuple<String,Float>> dayRank(int num){
        return null;
    }

    public List<TwoTuple<String,Float>> monthRank(int num){
        return null;
    }

    public List<TwoTuple<String,Float>> getTopN(int num){
        Random random=new Random();
        int begin = random.nextInt(allRanks.size()-num);

        return allRanks.subList(begin,begin+num);
    }
    public static void main(String[] args){

        TopItemRecommender topItemRecommender=new TopItemRecommender();
        Map<String,Long> his = new HashMap<>();
        his.put("lk",3l);
        his.put("clt",11l);
        his.put("v",100l);
        float s = ScoreEvaluator.evaluate(his,new Date().getTime());

        System.out.println(s);

        List<Integer> ll =new ArrayList<>();

        ll.add(3);
        ll.add(4);
        ll.add(5);
        ll.sort((x,y)-> x.compareTo(y));
        ll.forEach(x-> System.out.println(x));

    }

}
