package com.linghua.hdds.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TableUtil {
	
	private static Long MAX_VALUE_TIME=3000000000000000L;
	private static SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHH");


    public static String idReverseAndBuildWihtFourKey(String firstKey,String typeKey,String timeKey,String secondKey,String spaceMark){

        return new StringBuilder(firstKey).reverse().append(spaceMark).append(typeKey).append(spaceMark).append(timeKey).append(spaceMark).append(secondKey).toString();
    }

	public static String idReverseAndBuild(String firstKey,String timeKey,String secondKey,String spaceMark){

	    return new StringBuilder(firstKey).reverse().append(spaceMark).append(timeKey).append(spaceMark).append(secondKey).toString();
    }
    public static String idReverseAndBuild(String firstKey,String timeKey,String secondKey){

        return idReverseAndBuild( firstKey, timeKey, secondKey,"_");
    }
    public static String idReverseAndBuildWithoutTime(String firstKey,String secondKey,String spaceMark){
        return new StringBuilder(firstKey).reverse().append(spaceMark).append(secondKey).toString();
    }
    public static String idReverseAndBuild(String firstKey){

        return new StringBuilder(firstKey).reverse().toString();
    }
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

        System.out.println(idReverseAndBuild("000000000066","20140826072122","5465465465465464"));

	}

}
