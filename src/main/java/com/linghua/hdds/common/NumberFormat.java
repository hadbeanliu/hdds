package com.linghua.hdds.common;

import java.text.DecimalFormat;

public class NumberFormat {
	
	private static DecimalFormat df = new DecimalFormat("#.00");
	public static String decimalFormat(double number){
		
		return df.format(number);
	}
		

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
