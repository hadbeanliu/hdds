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
        startRecommend();

    }
    public static void startRecommend(){
        try {
            Table table = ConnectionFactory.createConnection().getTable(TableName.valueOf("headlines:user_msg_table"));
            Delete delete =new Delete("4600000000000000".getBytes());
            delete.addFamily("g".getBytes());
            table.delete(delete);
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
