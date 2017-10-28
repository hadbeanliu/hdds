package com.lhb.dds.hdds;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StringToIndex {

    public static void main(String[] args) throws IOException {

        BufferedReader read =new BufferedReader(new InputStreamReader(new FileInputStream(new File("/home/hadoop/result/tags.txt"))));
        List<String> subList = new ArrayList<>();
        String tmp ="";
        int cnt =0;
        while((tmp=read.readLine())!=null){
            cnt++;
            String[] split =tmp.split("/004");
            if(split.length ==2)
            subList.add(split[0]);
            else System.out.println(tmp);
        }
        System.out.println(cnt+"---"+subList.size());
        PrintWriter write =new PrintWriter(new FileOutputStream(new File("/home/hadoop/result/tagsIndex.model"),true));
        System.out.println("totla:"+subList.size());
        int length = subList.size();
        for(int i =0;i<length;i++)
            write.println(subList.get(i)+"/004"+i);
        write.flush();
        write.close();

    }
}
