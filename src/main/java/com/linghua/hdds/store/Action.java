package com.linghua.hdds.store;

import com.google.common.collect.Maps;
import com.linghua.hdds.meta.Hcolumn;

import java.util.Map;

public class Action implements BeanTemplate{

    public static final Map<Integer, Hcolumn> HBASE_MAPPING = Maps.newHashMap();

    static {

        HBASE_MAPPING.put(0, new Hcolumn("f".getBytes(), "sc".getBytes(), Hcolumn.Type.STRING));//收藏
        HBASE_MAPPING.put(1, new Hcolumn("f".getBytes(), "pf".getBytes(), Hcolumn.Type.DOUBLE));//评分
        HBASE_MAPPING.put(2, new Hcolumn("f".getBytes(), "gz".getBytes(), Hcolumn.Type.STRING));//关注
        HBASE_MAPPING.put(3, new Hcolumn("f".getBytes(),"yd".getBytes(), Hcolumn.Type.STRING));//阅读

        HBASE_MAPPING.put(4, new Hcolumn("gl".getBytes(), Hcolumn.Type.STRING));//关联文章
        HBASE_MAPPING.put(5, new Hcolumn("kw".getBytes(), Hcolumn.Type.FLOAT));//标签

    }
    @Override
    public Map<Integer, Hcolumn> getMapping() {
        return null;
    }

    @Override
    public String[] getAllField() {
        return new String[0];
    }

    @Override
    public Object get(int index) {
        return null;
    }

    @Override
    public int getFieldsCount() {
        return 0;
    }

    @Override
    public void put(int field, Object o) {

    }
}
