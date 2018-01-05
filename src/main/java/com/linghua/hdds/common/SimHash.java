package com.linghua.hdds.common;

import org.lionsoul.jcseg.tokenizer.core.JcsegException;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimHash {

    private String tokens;
    private static final int hashbits=64;
    private static final int distance=5;


    public SimHash(String tokens) {
        this.tokens = tokens;
    }

    public SimHash(String tokens, int hashbits) {
        this.tokens = tokens;
    }

    public Map<String,Integer> tokenize(){

        MainWordExtractor extractor=MainWordExtractor.getInstance();
        Map<String,Integer> freq=new HashMap<>();
        List<String> words= null;
        try {
            words = extractor.simpleTokenize(tokens);

            words.forEach(w->{
                freq.put(w,freq.getOrDefault(w,0)+1);
            });

           return freq;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JcsegException e) {
            e.printStackTrace();
        }


        return null;
    }


    public BigInteger simHash(Map<String,Integer> freq){

        int[] v=new int[hashbits];
        for (Map.Entry<String,Integer> wf: freq.entrySet()) {
            String word=wf.getKey();
            Integer weight=wf.getValue();
            BigInteger hash=shifHash(word);
            for (int i = 0; i <hashbits; i++) {
                BigInteger bitMask=new BigInteger("1").shiftLeft(i);
                if(hash.and(bitMask).signum() !=0)
                    v[i] +=weight;
                else v[i] -=weight;
            }
        }

        BigInteger fingerPrint=new BigInteger("0");
        for (int i = 0; i < hashbits; i++) {
            if (v[i] >= 0){
                fingerPrint = fingerPrint.add(new BigInteger("1").shiftLeft(i));   // update the correct fingerPrint
            }
        }
        return fingerPrint;
    }

    public BigInteger shifHash(String str){

        if(str==null||str.length()==0)
            return new BigInteger("0");
        else{

            char[] sourceChar=str.toCharArray();
            BigInteger x=BigInteger.valueOf(sourceChar[0]<<7);
            BigInteger m=new BigInteger("31");
            for (char c:sourceChar)
                x = x.multiply(m).add(BigInteger.valueOf(c));
            BigInteger mask =new BigInteger("2").pow(hashbits).subtract(new BigInteger("1"));
            boolean flag=true;
            for (char c:sourceChar){
                if (flag)
                    x=x.multiply(m).xor(BigInteger.valueOf(c<<3)).and(mask);
                else x=x.multiply(m).xor(BigInteger.valueOf(c>>3)).and(mask);
                flag = !flag;
            }
            if (x.equals(new BigInteger("-1"))){
                x = new BigInteger("-2");
            }

            return x;
        }


    }
    public String getFingerPrint(String str){
        int len = str.length();
        for (int i = 0; i < hashbits; i++) {
            if (i >= len){
                str = "0" + str;
            }
        }
        return str;
    }

    public BigInteger getSimHash(){

        return simHash(tokenize());
    }

    public static  BigInteger m= new BigInteger("1").shiftLeft(hashbits).subtract(new BigInteger("1"));

    public static boolean matcher(BigInteger first,BigInteger second){
        BigInteger x = first.xor(second).and(m);
        int tot = 0;
        while (x.signum() != 0){
            tot += 1;
            x = x.and(x.subtract(new BigInteger("1")));
        }
        if(tot<= distance)
            return true;

        return false;

    }
    public int getHarmingDistance(SimHash simhash){

        BigInteger m= new BigInteger("1").shiftLeft(hashbits).subtract(new BigInteger("1"));
        BigInteger x = getSimHash().xor(simhash.getSimHash()).and(m);
        int tot = 0;
        while (x.signum() != 0){
            tot += 1;
            x = x.and(x.subtract(new BigInteger("1")));
         }
        return tot;

    }
//3065660695
//3063547031
    //6977518874744472855
//6986706965134705815
    public static void main(String[] args){

//        try (Connection conn = ConnectionFactory.createConnection()) {
//            Table table=conn.getTable(TableName.valueOf("headlines:item_meta_table"));
//            Scan scan=new Scan();
//            scan.addColumn("p".getBytes(),"cnt".getBytes());
//            table.getScanner().iterator()
//        }
        String content1="7种情形为：入学未满一学期或者毕业前一年的;高考成绩低于拟转入学校相关专业同一生源地相应年份录取成绩的;由低学历层次转为高学历层次的;以定向就业招生录取的;研究生拟转入学校、专业的录取控制标准高于其所在学校、专业的;无正当转学理由的;学校规定的其他限制性情形的";

        String content2="7种情形为：入学未满一学期或者毕业前一年的;高考成绩低于拟转入学校相关专业同一生源地相应年份录取成绩的;由低学历层次转为高学历层次的;以定向就业招生录取的;研究生拟转入学校、专业的录取控制标准高于其所在学校、专业的;无正当转学理由的;学校规定的其他限制性情形的。微信给你送红包！扫码开发票就能领，最高能拿 188 元";
//        String contetn3="提供全流程网上办理（在线申请、网上预审、网上受理、网上办结）,申请人不用提交纸质申请材料，只须办结后领取结果";
        long begin =System.currentTimeMillis();
        for (int i = 0; i <1000000; i++) {
            SimHash hash1=new SimHash(content1);
            SimHash hash2=new SimHash(content2);
            hash1.getHarmingDistance(hash2);
            if(i == 5000)
                System.out.println(i);
        }
        System.out.println(System.currentTimeMillis() - begin);
//        SimHash hash1=new SimHash(content1);
//        SimHash hash2=new SimHash(content2);


//        System.out.println(hash1.getHarmingDistance(hash2));
//        BigInteger c1=new BigInteger("6977518874744472855");
//        BigInteger c2=new BigInteger("3063547031");
//        BigInteger m= new BigInteger("1").shiftLeft(hashbits).subtract(new BigInteger("1"));
//////
//        BigInteger x = c1.xor(c2).and(m);
//        int tot = 0;
//        while (x.signum() != 0){
//            tot += 1;
//            x = x.and(x.subtract(new BigInteger("1")));
//        }
//        System.out.println(tot);
    }
}
//0110000011010101001000000111001010110110101110100100010100010111