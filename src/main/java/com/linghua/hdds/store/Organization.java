package com.linghua.hdds.store;

import java.util.Map;

import com.google.common.collect.Maps;
import com.linghua.hdds.meta.Hcolumn;
import com.linghua.hdds.meta.Hcolumn.Type;

public class Organization implements BeanTemplate {
	
	
	public static final Map<Integer, Hcolumn> HBASE_MAPPING = Maps.newHashMap();

	static {

		HBASE_MAPPING.put(0, new Hcolumn("f".getBytes(), null, Type.STRING));
		HBASE_MAPPING.put(1, new Hcolumn("dy".getBytes(), Type.FLOAT));
		HBASE_MAPPING.put(2, new Hcolumn("mk".getBytes(), Type.INT));
		HBASE_MAPPING.put(3, new Hcolumn("kw".getBytes(),Type.STRING));

	}
	public static enum FIELDS {
		INFO("info",0),SUBSCRIPTION("subscription",1),MARK("mark",2),KEYWORD("keyword",3);
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
	public static final String[] ALL_FIELDS = { "info", "subscription", "mark" };
	
	private String id;
	private Map<String, Float> subscription;
	private Map<String, String> info;
	private Map<String, Integer> mark;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, Float> getSubscription() {
		return subscription;
	}

	public void setSubscription(Map<String, Float> subscription) {
		this.subscription = subscription;
	}

	public Map<String, String> getInfo() {
		return info;
	}

	public void setInfo(Map<String, String> info) {
		this.info = info;
	}

	public Map<String, Integer> getMark() {
		return mark;
	}

	public void setMark(Map<String, Integer> mark) {
		this.mark = mark;
	}

	@Override
	public Object get(int index) {

		switch (index) {

		case 0:
			return info;
		case 1:
			return subscription;
		case 2:
			return mark;
		default:
			throw new IndexOutOfBoundsException("Bad Index");

		}

	}

	@Override
	public int getFieldsCount() {
		return Organization.ALL_FIELDS.length;
	}

	@Override
	public void put(int field, Object o) {
		switch (field) {

		case 0:
			info = (Map<String, String>) o;
			break;
		case 1:
			subscription = (Map<String, Float>) o;
			break;
		case 2:
			mark = (Map<String, Integer>) o;
			break;
//		case 3:
//			keyword = (Map<String, String>) o;
//			break;

		default:
			throw new IndexOutOfBoundsException("Bad Index");

		}

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
