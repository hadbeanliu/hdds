package test;


import com.linghua.hdds.common.HttpClientResource;
import com.linghua.hdds.common.TableUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Test {

    public static void main(String[] args){

        List<Integer> list=new ArrayList<>();
        list.add(1);
        list.add(3);
        list.add(5);
        list.add(2);
        list.sort((x,y) -> y.compareTo(x));
        System.out.println(list);

    }
    public static void startRecommend(){
        try {
            Table table = ConnectionFactory.createConnection().getTable(TableName.valueOf("headlines:item_meta_table"));
            Get get = new Get(TableUtil.IdReverse("2017112019006656").getBytes());
            Result result =table.get(get);
            String content = Bytes.toString(result.getValue("p".getBytes(),"cnt".getBytes()));
            String rs = HttpClientResource.post(content,
                    "http://slave2:9999/mining/classify?biz_code=" + "headlines"
                            + "&ss_code=user-test&model=NaiveBayes");
            System.out.println(rs);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
