package com.linghua.hdds.preference.model;

public class BaseUserVoteModel {

	private static BaseUserVoteModel model;
	
//	private 
	
	
	public BaseUserVoteModel getInstance(){
		
		return null;
	}	
	
	
}
interface Calculate{
	
	
	public default double calc(SparseVector v){
		
		
		return v.indics.length;
	}
	
	public double timeEffect(long t);
	
}