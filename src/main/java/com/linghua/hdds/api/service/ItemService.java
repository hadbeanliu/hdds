package com.linghua.hdds.api.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.linghua.hdds.api.db.HbaseDaoImpl;
import com.linghua.hdds.common.HbaseMetaFactory;
import com.linghua.hdds.meta.Hcolumn;
import com.linghua.hdds.store.Item;


@Service("ItemService")
public class ItemService extends ServiceTemplate<Item>{

	private static String DEFAULT_MAPPER_TABLE = "item_meta_table";
	
	public Item mapRow(Result result) throws Exception{
		return this.mapper.mapRow(result, 0);
	}

	private RowMapper<Item> mapper = new RowMapper<Item>() {
		
		@Override
		public Item mapRow(Result result, int rowNum) throws Exception {
			Item item = new Item();
			item.setId(Bytes.toString(result.getRow()));
			
			for(Entry<Integer, Hcolumn> entry:Item.HBASE_MAPPING.entrySet()){ 
				HbaseDaoImpl.setField(result, item, entry);
			}
			
			return item;
		}

	};

	
	public List<Item> get(final String bizCode,final List<String> ids,final Set<Item.FIELDS> fields){
		
		return ht.get(getTablename(bizCode), ids, mapper);
	}
	
	public Item get(final String bizCode,final String id, final Collection<Hcolumn> fields) {

		return ht.get(getTablename(bizCode), id,fields,getMapper());
	}
		
	
	public Item get(final String bizCode,final String id, final Item.FIELDS fields){
		
		return ht.get(getTablename(bizCode), id, new String(Item.HBASE_MAPPING.get(fields.getIndex()).getFamily()), mapper);
	}
	
	
	public List<Item> find(final String bizCode, final Set<Hcolumn> fields,Filter filter,
			final int hm) {

		Scan scan = HbaseMetaFactory.createScan(null, filter, fields);
		
//		if (hm > 0)
//			scan.setFilter(new PageFilter(hm));
		return ht.find(getTablename(bizCode), scan, mapper);
	}
	
	public List find(final String bizCode, final Set<Hcolumn> fields,final Filter filter,
			String startRow,String endRow,final int hm,RowMapper mapper) {
		
		Scan scan = HbaseMetaFactory.createScan(null,startRow,endRow, filter, fields);
		
		if (hm > 0)
			scan.setFilter(new PageFilter(hm));
		return ht.find(getTablename(bizCode), scan, mapper);
	}
	
	public List<Item> find(final String bizCode, final Set<Hcolumn> fields,
			final int hm) {

		
		return find(bizCode,fields,null,hm);
	}
	
	
	

	
	public String put(String bizCode,String[] rowNames,Item.FIELDS field,String value){
		
		Assert.notNull(field,"field is not defined!!");
		List<Put> puts=new ArrayList<>();
		Hcolumn col=Item.HBASE_MAPPING.get(field.getIndex());
		for(String row:rowNames){
			Put put=new Put(Bytes.toBytes(row));
			put.add(col.getFamily(), col.getQualifier(), Bytes.toBytes(value));
			puts.add(put);
		}
		try{
		ht.put(getTablename(bizCode), puts);
		}catch(Exception e){
			e.printStackTrace();
			return "0";
		}
		return "1";
	}
	

	@Override
	protected String getTablename(String bizCode) {
		return bizCode+":"+DEFAULT_MAPPER_TABLE;
	}

	@Override
	protected RowMapper<Item> getMapper(){
		return this.mapper;
	}
}
