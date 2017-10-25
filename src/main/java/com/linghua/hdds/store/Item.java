package com.linghua.hdds.store;

import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.client.Put;

import com.google.common.collect.Maps;
import com.linghua.hdds.meta.Hcolumn;
import com.linghua.hdds.meta.Hcolumn.Type;


public class Item implements BeanTemplate{
	
	
	public static final Map<Integer, Hcolumn> HBASE_MAPPING = Maps.newHashMap();

	static {
		
		HBASE_MAPPING.put(0, new Hcolumn("p".getBytes(), "upd".getBytes(),Type.STRING));//更新日期
		HBASE_MAPPING.put(1, new Hcolumn("p".getBytes(), "t".getBytes(),Type.STRING));//标题
		HBASE_MAPPING.put(2, new Hcolumn("p".getBytes(), "ft".getBytes(),Type.STRING));//第一次采集时间
		HBASE_MAPPING.put(3, new Hcolumn("p".getBytes(),"cnt".getBytes(),Type.STRING));//内容

		HBASE_MAPPING.put(4, new Hcolumn("gl".getBytes(),Type.STRING));//关联文章
		HBASE_MAPPING.put(5, new Hcolumn("kw".getBytes(), Type.FLOAT));//标签
		
		HBASE_MAPPING.put(6, new Hcolumn("s".getBytes(), "s".getBytes(),Type.DOUBLE));//得分
		HBASE_MAPPING.put(7, new Hcolumn("ts".getBytes(),Type.STRING));//人群推送
		
		HBASE_MAPPING.put(8, new Hcolumn("f".getBytes(),"lb".getBytes(),Type.STRING));//类别
		HBASE_MAPPING.put(9, new Hcolumn("f".getBytes(),"zd".getBytes(),Type.LONG));//置顶
		HBASE_MAPPING.put(10, new Hcolumn("f".getBytes(),"tj".getBytes(),Type.FLOAT));//推荐评分
		HBASE_MAPPING.put(11, new Hcolumn("f".getBytes(), "fp".getBytes(),Type.STRING));//首次发布时间
		HBASE_MAPPING.put(12, new Hcolumn("f".getBytes(),"lbId".getBytes(),Type.STRING));//类别ID
		HBASE_MAPPING.put(13, new Hcolumn("f".getBytes(),"pId".getBytes(),Type.STRING));//父类别ID
		HBASE_MAPPING.put(14, new Hcolumn("f".getBytes(),"pLb".getBytes(),Type.STRING));//父类别ID

		HBASE_MAPPING.put(15, new Hcolumn("meta".getBytes(),Type.STRING));//meta
		HBASE_MAPPING.put(16, new Hcolumn("sys".getBytes(),Type.STRING));
		HBASE_MAPPING.put(17,new Hcolumn("his".getBytes(),Type.LONG));

		
	}
	
	public static String[] ALL_FIELDS={"update","title","firstFetchTime","content","related","keyword",
								"score","pushCrowd","catagory","stickTop","manualScore","firstPubTime","catagoryId","pCatagory","pCatagoryId","tag","system","history"};

	public static enum FIELDS {
		UPDATE("update",0),TITLE("title",1),FIRSTFETCHTIME("firstFetchTime",2),CONTENT("content",3),
		RELATED("related",4),KEYWORD("keyword",5),SCORE("score",6),PUSHCROWD("pushCrowd",7),CATAGORY("catagory",8),
		STICKTOP("stickTop",9),MANUALSCORE("manualScore",10),FIRSTPUBTIME("firstPubTime",11),CATAGORYID("lbId",12),PCATAGORY("pLb",13),PCATAGORYID("pId",14),META("meta",15),SYS("system",16),HIS("history",17);
		
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

		private String id;
		//"update","title","firstFetchTime","content","related","keyword",
		//"score","pushCrowd","catagory","stickTop","manualScore","firstPubTime"
		private String update;
		
		private String title;
		private String firstFetchTime;
		private String content;
		
		private Map<String, Float> related;
		private Map<String, Float> keyword;		
		private double score;
		private Map<String,String> pushCrowd;
		private String catagory;
		private long stickTop;
		private float manualScore;
		private String firstPubTime;
		private String catagoryId;
		private String pCatagory;
		private String pCatagoryId;
		
		
		private Map<String, String> meta;
		
		private Map<String,String> sys;
		private Map<String,Long> history;
		// item's main feature for recommend
		
//		private CharSequence place;
//		private CharSequence ageRange;
//		private CharSequence sex;
//		private CharSequence profession;
//		private CharSequence preference;
		
		


		

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
		
		
		

		public String getUpdate() {
			return update;
		}

		public void setUpdate(String update) {
			this.update = update;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getFirstFetchTime() {
			return firstFetchTime;
		}

		public void setFirstFetchTime(String firstFetchTime) {
			this.firstFetchTime = firstFetchTime;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public Map<String, Float> getRelated() {
			return related;
		}

		public void setRelated(Map<String, Float> related) {
			this.related = related;
		}

		public Map<String, Float> getKeyword() {
			return keyword;
		}

		public void setKeyword(Map<String, Float> keyword) {
			this.keyword = keyword;
		}

		public double getScore() {
			return score;
		}

		public void setScore(double score) {
			this.score = score;
		}

		public Map<String,String> getPushCrowd() {
			return pushCrowd;
		}

		public void setPushCrowd(Map<String,String> pushCrowd) {
			this.pushCrowd = pushCrowd;
		}

		public String getCatagory() {
			return catagory;
		}

		public void setCatagory(String catagory) {
			this.catagory = catagory;
		}

		public long getStickTop() {
			return stickTop;
		}

		public void setStickTop(long stickTop) {
			this.stickTop = stickTop;
		}

		public float getManualScore() {
			return manualScore;
		}

		public void setManualScore(float manualScore) {
			this.manualScore = manualScore;
		}

		public String getFirstPubTime() {
			return firstPubTime;
		}

		public void setFirstPubTime(String firstPubTime) {
			this.firstPubTime = firstPubTime;
		}
		
		

		public String getCatagoryId() {
			return catagoryId;
		}

		public void setCatagoryId(String catagoryId) {
			this.catagoryId = catagoryId;
		}

		public String getpCatagory() {
			return pCatagory;
		}

		public void setpCatagory(String pCatagory) {
			this.pCatagory = pCatagory;
		}

		public String getpCatagoryId() {
			return pCatagoryId;
		}

		public void setpCatagoryId(String pCatagoryId) {
			this.pCatagoryId = pCatagoryId;
		}

		public Map<String, String> getMeta() {
			return meta;
		}

		public void setMeta(Map<String, String> meta) {
			this.meta = meta;
		}
		
		

		public Map<String, String> getSys() {
			return sys;
		}

		public void setSys(Map<String, String> sys) {
			this.sys = sys;
		}

	public Map<String, Long> getHistory() {
		return history;
	}

	public void setHistory(Map<String, Long> history) {
		this.history = history;
	}

	@Override
		public boolean isDirty(int index) {
			
			return get(index)!=null;
		}

		//UPDATE("update",0),TITLE("title",1),FIRSTFETCHTIME("firstFetchTime",2),CONTENT("content",3),
		//RELATED("related",4),KEYWORD("keyword",5),SCORE("score",6),PUSHCROWD("pushCrowd",7),CATAGORY("catagory",8),
		//STICKTOP("stickTop",9),MANUALSCORE("manualScore",10),FIRSTPUBTIME("firstPubTime",11)
		@Override
		public Object get(int index) {
			switch(index){
			case 0: return this.update;
			case 1: return this.title;
			case 2: return this.firstFetchTime;
			case 3: return this.content;
			case 4: return this.related;
			case 5: return this.keyword;
			case 6: return this.score;
			case 7: return this.pushCrowd;
			case 8: return this.catagory;
			case 9: return this.stickTop;
			case 10: return this.manualScore;
			case 11:return this.firstPubTime;
			case 12:return this.catagoryId;
			case 13:return this.pCatagory;
			case 14:return this.pCatagoryId;
			case 15:return this.meta;
			case 16:return this.sys;
			case 17:return this.history;

			default:throw new RuntimeException("Bad Index:"+index);
			}
		}

		@Override
		public int getFieldsCount() {
			
			return Item.ALL_FIELDS.length;
		}

		@Override
		public void put(int field, Object o) {
			switch(field){
			case 0:  this.update=(String) o;break;
			case 1:  this.title=(String) o;break;
			case 2:  this.firstFetchTime=(String) o;break;
			case 3:  this.content=(String) o;break;
			case 4:  this.related=(Map<String, Float>) o;break;
			case 5:  this.keyword=(Map<String, Float>) o;break;
			case 6:  this.score=(double) o;break;
			case 7:  this.pushCrowd=(Map<String,String>) o;break;
			case 8:  this.catagory=(String) o;break;
			case 9:  this.stickTop=(long) o;break;
			case 10: this.manualScore=(float) o;break;
			case 11: this.firstPubTime=(String) o;break;
			case 12: this.catagoryId=(String) o;break;
			case 13: this.pCatagory=(String) o;break;
			case 14: this.pCatagoryId=(String) o;break;
			case 15: this.meta=(Map<String, String>) o;break;
			case 16: this.sys=(Map<String, String>) o;break;
			case 17: this.history=(Map<String, Long>) o;break;
			default:throw new RuntimeException("Bad Index");
			}
			
		}
		
	public Put toPut(){
		
		
		
		return null;
	}

	@Override
	public Map<Integer, Hcolumn> getMapping() {
		
		return this.HBASE_MAPPING;
	}

	@Override
	public String[] getAllField() {
		return ALL_FIELDS;
	}


}
