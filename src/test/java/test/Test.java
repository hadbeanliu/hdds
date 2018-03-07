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

//        final byte[] newby = new byte[Integer.MAX_VALUE/2];

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
