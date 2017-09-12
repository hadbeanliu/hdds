package com.linghua.hdds.common;

import java.util.Set;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.springframework.util.StringUtils;

import com.linghua.hdds.meta.Hcolumn;


public class HbaseMetaFactory {
	
	
	public static Scan createScan(Scan scan,Filter filter,Set<Hcolumn> fields){
		
		return createScan( scan,null,null, filter,fields);
	}
	
	public static Scan createScan(Scan scan,String startRow,String endRow,Filter filter,Set<Hcolumn> fields){
		if(scan==null)
			scan=new Scan();
		if(!StringUtils.isEmpty(startRow))
			scan.setStartRow(startRow.getBytes());
		if(!StringUtils.isEmpty(endRow))
			scan.setStopRow(endRow.getBytes());
		
		if(filter!=null)
		    scan.setFilter(filter);
		if(fields!=null){
			for(Hcolumn col:fields){
				if(col.getFamily()==null)
					throw new IllegalArgumentException("family cant be null");
				if(col.getQualifier()==null)
					scan.addFamily(col.getFamily());
				else scan.addColumn(col.getFamily(), col.getQualifier());
					
				
			}
			
		}
		return scan;
	}
	
	
	public static Get createGet(Get get,String rowKey,Set<Hcolumn> fields){
		if(get ==null)
			get=new Get(rowKey.getBytes());
		if(fields!=null){
			for(Hcolumn col:fields){
				if(col.getFamily()==null)
					throw new IllegalArgumentException("family cant be null");
				if(col.getQualifier()==null)
					get.addFamily(col.getFamily());
				else get.addColumn(col.getFamily(), col.getQualifier());
					
			}
			
		}
		
		return get;
		
	}

}
