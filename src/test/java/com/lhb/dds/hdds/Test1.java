package com.lhb.dds.hdds;

import java.util.Arrays;

import com.google.gson.Gson;
import com.lhb.dds.hdds.A.FIELDS;
import com.linghua.hdds.api.response.UserVo;
import com.linghua.hdds.store.User;

public class Test1 {

	 public static void main(String[] args){
		 
		UserVo vo=new UserVo();
		
		vo.setAge("1991/08/08");
		vo.setGender("m");
		vo.setLocation("福州");
		vo.setOid("linghua");
		vo.setUid("0000A");
		vo.setName("罗杰");
		vo.setSub(Arrays.asList(new String[]{"体育","编程"}));
		vo.setGraph(Arrays.asList(new String[]{"杜鹃","鸟类","声之形"}));
				 
		 System.out.println(new Gson().toJson(vo));
		 
	 }
	
}
abstract class A{
	
	public static final String[] ALL_FIELD={};
	public static enum FIELDS {
		UPDATE("update",0),TITLE("title",1),FIRSTFETCHTIME("firstFetchTime",2),CONTENT("content",3),
		RELATED("related",4),KEYWORD("keyword",5),SCORE("score",6),PUSHCROWD("pushCrowd",7),CATAGORY("catagory",8);
		
		private String name;
		private int index;

		FIELDS(String name, int index) {
			this.name = name;
			this.index = index;
		
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public String toString(){
			return name;
		}
		
	}
}

class B extends A{
	
	public static final FIELDS FIELDS = null;
	public static final String[] ALL_FIELD={"1","2"};
	public static enum FIELDS {
		UPDATE("update",0),TITLE("title",1),FIRSTFETCHTIME("firstFetchTime",2),CONTENT("content",3),
		RELATED("related",4),KEYWORD("keyword",5),SCORE("score",6),PUSHCROWD("pushCrowd",7),CATAGORY("catagory",8),
		STICKTOP("stickTop",9),MANUALSCORE("manualScore",10),FIRSTPUBTIME("firstPubTime",11),CATAGORYID("lbId",12),PCATAGORY("pLb",13),PCATAGORYID("pId",14),META("meta",15),SYS("system",16);
		
		private String name;
		private int index;

		FIELDS(String name, int index) {
			this.name = name;
			this.index = index;
		
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public String toString(){
			return name;
		}
		
	}
	
}