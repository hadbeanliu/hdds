package com.linghua.hdds.common;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.util.Bytes;

public class CellToObject {

	public static String familyToString(Cell e){
		
		return Bytes.toString(CellUtil.cloneFamily(e));
		
	}
	
	public static String qualifierToString(Cell e){
		
		return Bytes.toString(CellUtil.cloneQualifier(e));
		
	}
	public static String valueToString(Cell e){
		
		return Bytes.toString(CellUtil.cloneValue(e));
		
	}
	
}
