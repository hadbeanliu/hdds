package test;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class SpecialTest {

    public static void main(String[] args){

        try {
            int count =0;
            long now =new Date().getTime();
            long aDay = 5*24*60*60*1000;
            List<String> badLine = new ArrayList<>();
            Table table = ConnectionFactory.createConnection().getTable(TableName.valueOf("headlines:user_act_item_table"));
            Scan scan = new Scan();
            scan.setTimeRange(now-aDay,now);
            ResultScanner scanner = table.getScanner(scan);
            Iterator<Result> rs = scanner.iterator();
            while(rs.hasNext()){
                count++;
                rs.next();
            }
            System.out.println(count);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
