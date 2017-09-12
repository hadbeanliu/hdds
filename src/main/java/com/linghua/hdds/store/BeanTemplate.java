package com.linghua.hdds.store;

import java.util.Map;

import com.linghua.hdds.meta.Hcolumn;

public interface BeanTemplate {
	

	public default boolean isDirty(int index){

		return get(index) != null;
	
	}
	
	public Map<Integer, Hcolumn> getMapping();
	
	public String[] getAllField();
	
	public Object get(int index);
	
	public int getFieldsCount();
	
	public void put(int field,Object o);
}
