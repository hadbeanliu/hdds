package com.linghua.hdds.api.service;

import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.RowMapper;

import com.linghua.hdds.api.db.HbaseDaoImpl;
import com.linghua.hdds.common.HbaseMetaFactory;
import com.linghua.hdds.meta.Hcolumn;
import com.linghua.hdds.store.BeanTemplate;
import com.linghua.hdds.store.Item;
import com.linghua.hdds.store.Organization;

abstract class ServiceTemplate<T extends BeanTemplate> {

	@Autowired
	protected HbaseDaoImpl ht;

	protected abstract String getTablename(String bizCode);

	public void delete(String bizCode, String[] rows) {
		this.ht.delete(getTablename(bizCode), rows);

	}

	public void delete(String bizCode, String row, Hcolumn col) {

		if (col.getQualifier() == null)
			this.ht.delete(getTablename(bizCode), row, new String(col.getFamily()));
		else
			ht.delete(getTablename(bizCode), row, new String(col.getFamily()), new String(col.getQualifier()));
	}

	public void put(String bizCode, String key, T beanClass) {

		Put put = new Put(key.getBytes());
		for (int i = 0; i < beanClass.getFieldsCount(); i++) {
			if (beanClass.isDirty(i)) {
				Hcolumn col = beanClass.getMapping().get(i);
				if (col == null) {
					throw new RuntimeException("HBase mapping for field [" + beanClass.getClass().getName() + "#"
							+ beanClass.getAllField()[i] + "] not found. ");
				}
				HbaseDaoImpl.addPutsAndDeletes(put, null, beanClass.get(i), col);
			}

		}

		ht.put(getTablename(bizCode), put);
	}

	public List<T> find(final String bizCode, final Set<Hcolumn> fields, Filter filter, final int hm,
			final String lastRow) {
		Scan scan = HbaseMetaFactory.createScan(null, filter, fields);
		scan.setStartRow(lastRow.getBytes());

		return ht.find(getTablename(bizCode), scan, getMapper());

	}

	public T get(final String bizCode, final String id) {

		return ht.get(getTablename(bizCode), id, getMapper());
	}

	public T get(final String bizCode, final String id, Set<Hcolumn> filter){
	    return ht.get(getTablename(bizCode),id,filter,getMapper());
    }

	abstract RowMapper<T> getMapper();
}
