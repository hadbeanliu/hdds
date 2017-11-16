package com.lhb.dds.hdds;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class TextCount {

    public static void main(String[] args){
        try {
            BufferedReader reader =new BufferedReader(new FileReader(new File("/home/hadoop/result/placeCount.txt")));
            String str="";
            List<String> index=new ArrayList<>();
            Map<String,Integer> count =new HashMap<>();
            int cnt =0;
            int count2 =0;
            int count3=0;
            while((str=reader.readLine())!=null){
                cnt ++;
                String[] strs = str.split(",");
                if(strs.length<2||strs[1].equals(""))
                    continue;
                int length = strs[0].length();
                char last = strs[0].charAt(length-1);
                String name=String.valueOf(last);
                int value = Integer.parseInt(strs[1]);
                if(length<4){
                    switch (last){
                        case '县':
                        case '省':
                        case '市':
                        case '镇':
                        case '区': {name = 'L'+String.valueOf(last);count3+=value;break;}
                    }
                }
                count2+=value;
                count.put(name,count.getOrDefault(name,0)+value);
            }
            System.out.println((count3*1.0/count2)+"~~~"+cnt);
            List<Map.Entry<String,Integer>> r = new ArrayList<>(count.entrySet());
            r = r.stream().sorted((x,y) -> y.getValue().compareTo(x.getValue())).collect(Collectors.toList());
            PrintWriter write =new PrintWriter(new FileOutputStream(new File("/home/hadoop/result/count2.txt"),true));
            for(Map.Entry kv:r)
                write.println(kv.getKey()+","+kv.getValue());
            write.flush();
            write.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
