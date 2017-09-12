package com.linghua.hdds.common;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DateUtils {
	
	private static String  REGEX="yyyy-MM-dd";
	private static SimpleDateFormat format=new SimpleDateFormat(REGEX);
	protected static String pubDateRegex="\\d+[年/\\.-]+\\d+[月/\\.-]+\\d+[日]*(\\s+\\d+:?\\d+)?";
	private static Pattern p=Pattern.compile(pubDateRegex);

	public enum Params{
		BEGIN_TIME,
		DAY_COLLECT;		
	}

	public static String format(String regex,Date date){
		if(regex==null)
			regex=REGEX;
		SimpleDateFormat format=new SimpleDateFormat(regex);
		return format.format(date);
		
	}
	public static String getPubDate(String str){
		if(str==null)
			return null;
		Matcher ma=p.matcher(str);
		if(ma.find())
			return ma.group().replaceAll("[年月\\./]", "-").replaceAll("日", "");
		return null;
	}
	
	public static String parserToTime(long time){
		try{
		return format.format(new Date(time));
		}catch(Exception e){}
		return null;
	}
	
	
	public static long DateToNumber(Map<String,String[]> params,Params p){
		String begin_time=params.get("year0")[0]+"-"+params.get("mon0")[0]+"-"+params.get("day0")[0];
		Date temp=null;
		try {
			temp = format.parse(begin_time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println(begin_time+":"+temp.getTime());
		return temp.getTime();
				
				
	}

	public static String DateToJson(Map<String,String[]> params){
		
		return null;
	}
	
	public static String JsonToDate(String time){
		return null;
	}

	public static Date toDate(String regex,String time){
		if(regex==null)
			regex=REGEX;
		SimpleDateFormat format=new SimpleDateFormat(regex);
		try {
			return format.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String collectTimeToString(Map<String,String[]> params){
		StringBuffer day_collect=new StringBuffer();
		for(int i=0;i<3;i++){
		if(params.get("hour"+i)==null||params.get("min"+i)==null)
			continue;
		try{
			int hour=Integer.parseInt(params.get("hour"+i)[0]);
			int min=Integer.parseInt(params.get("min"+i)[0]);
			day_collect.append(hour+"-"+min).append(",");
		}catch(Exception e){
			System.err.println("输入数字");
		}
			}
		
		return day_collect.toString();
				
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DateUtils utils=new DateUtils();
		System.out.println(utils.getPubDate("时间\u0026nbsp;2015-04-15 16:29:22"));
	}

}
