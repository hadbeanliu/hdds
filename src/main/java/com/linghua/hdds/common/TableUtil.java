package com.linghua.hdds.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TableUtil {
	
	private static Long MAX_VALUE_TIME=3000000000000000L;
	private static SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHH");
	
	public static String getEndKey(int num,int field){
		switch(field){
		
		case Calendar.YEAR:{
					Calendar calendar=Calendar.getInstance();
					calendar.add(Calendar.YEAR, -1*num);;
					String id=format.format(calendar.getTime())+"000000";
					return IdReverse(id);
		}
		case Calendar.MONTH:{
			Calendar calendar=Calendar.getInstance();
			calendar.add(Calendar.MONTH, -1*num);;
			String id=format.format(calendar.getTime())+"000000";
			return IdReverse(id);
}
		case Calendar.DAY_OF_YEAR:{
			Calendar calendar=Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_YEAR, -1*num);;
			String id=format.format(calendar.getTime())+"000000";
			return IdReverse(id);
}
		case Calendar.WEEK_OF_YEAR:{
			Calendar calendar=Calendar.getInstance();
			calendar.add(Calendar.WEEK_OF_YEAR, -1*num);;
			String id=format.format(calendar.getTime())+"000000";
			
			return IdReverse(id);
}
		default: throw new RuntimeException("unknow field");
		
		}
		
	}
	
	public static String IdReverse(String id){
		
		return Long.toString(MAX_VALUE_TIME-Long.valueOf(id));
	
	}
	
	public static void main(String[] args){
		
		System.out.println(IdReverse(getEndKey(1, Calendar.YEAR)));
  	  	System.out.println(TableUtil.IdReverse("982988380999966"));
  	  	System.out.println(123123);

	}

}
