package com.linghua.hdds.store;

import java.util.Map;

import com.google.common.collect.Maps;
import com.linghua.hdds.meta.Hcolumn;
import com.linghua.hdds.meta.Hcolumn.Type;

public class User implements BeanTemplate {

	public static final Map<Integer, Hcolumn> HBASE_MAPPING = Maps.newHashMap();

	static {

		HBASE_MAPPING.put(0, new Hcolumn("f".getBytes(), null, Type.STRING));
		HBASE_MAPPING.put(1, new Hcolumn("g".getBytes(), Type.LONG));
		HBASE_MAPPING.put(2, new Hcolumn("r".getBytes(), Type.STRING));
		HBASE_MAPPING.put(3, new Hcolumn("rec".getBytes(), Type.FLOAT));
		HBASE_MAPPING.put(4, new Hcolumn("bhv".getBytes(), Type.LONG));
		HBASE_MAPPING.put(5, new Hcolumn("mk".getBytes(), Type.STRING));
		HBASE_MAPPING.put(6, new Hcolumn("dy".getBytes(), Type.FLOAT));

	}

	public static enum FIELDS {
		INFO("f", 0), GRAPH("g", 1), RECORED("r", 2), RECOMMEND("rec", 3), BEHAVIOR(
				"bhv", 4), MARK("mk", 5),SUBSCRIPTION("subscription",6);

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

		public String toString() {
			return name;
		}

	}

	public static final String[] ALL_FIELDS = { "info", "graph", "recored",
			"recommend", "behavior", "mark","subscription" };

	public int getFieldsCount() {

		return User.ALL_FIELDS.length;
	}

	public Object get(int index) {

		switch (index) {

		case 0:
			return info;
		case 1:
			return graph;
		case 2:
			return recored;
		case 3:
			return recommend;
		case 4:
			return behavior;
		case 5:
			return mark;
		case 6:
			return subscription;

		default:
			throw new IndexOutOfBoundsException("Bad Index");

		}

	}

	@Override
	public void put(int field, Object o) {
		switch (field) {

		case 0:
			info = (Map<String, Object>) o;
			break;
		case 1:
			graph = (Map<String, Long>) o;
			break;
		case 2:
			recored = (Map<String, String>) o;
			break;
		case 3:
			recommend = (Map<String, String>) o;
			break;
		case 4:
			behavior = (Map<String, Long>) o;
			break;

		case 5:
			mark = (Map<String, String>) o;
			break;
		case 6:
			subscription=(Map<String, Float>) o;
			break;
		default:
			throw new IndexOutOfBoundsException("Bad Index");

		}

	}

	private String id;
	private Map<String, Object> info;
	private Map<String, Long> graph;

	private Map<String, String> recored;
	private Map<String, String> recommend;

	private Map<String, Long> behavior;

	private Map<String, String> mark;
	
	private Map<String, Float> subscription;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, Object> getInfo() {

		return info;
	}

	public void setInfo(Map<String, Object> info) {
		this.info = info;
	}

	public Map<String, Long> getGraph() {

		return graph;
	}

	public void setGraph(Map<String, Long> graph) {
		this.graph = graph;
	}

	public Map<String, String> getRecored() {

		return recored;
	}

	public void setRecored(Map<String, String> recored) {
		this.recored = recored;
	}

	public Map<String, String> getRecommend() {

		return recommend;
	}

	public void setRecommend(Map<String, String> recommend) {
		this.recommend = recommend;
	}

	public Map<String, Long> getBehavior() {

		return behavior;
	}

	public void setBehavior(Map<String, Long> behavior) {

		this.behavior = behavior;
	}

	public Map<String, String> getMark() {
		return mark;
	}

	public void setMark(Map<String, String> mark) {
		this.mark = mark;
	}

	public Map<String, Float> getSubscription() {
		return subscription;
	}

	public void setSubscription(Map<String, Float> subscription) {
		this.subscription = subscription;
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
