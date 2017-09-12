package com.linghua.hdds.api.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Service;

import com.linghua.hdds.api.db.HbaseDaoImpl;
import com.linghua.hdds.common.HbaseMetaFactory;
import com.linghua.hdds.meta.Hcolumn;
import com.linghua.hdds.store.User;


@Service("UserService")
public class UserService extends ServiceTemplate<User>{

	private String DEFAULT_MAPPER_TABLE = "user_msg_table";
	private static Set<Hcolumn> FIELDS = new HashSet<Hcolumn>();
	

	private RowMapper<User> mapper = new RowMapper<User>() {
		
		@Override
		public User mapRow(Result result, int rowNum) throws Exception {
			User u = new User();
			u.setId(Bytes.toString(result.getRow()));
			
			for(Entry<Integer, Hcolumn> entry:User.HBASE_MAPPING.entrySet()){ 
				HbaseDaoImpl.setField(result, u, entry);
			}
			
			return u;
		}

	};

	public List<User> find(final String bizCode, final Set<Hcolumn> fields,
			final int hm) {

		Scan scan = HbaseMetaFactory.createScan(null, null, fields);
		if (hm > 0)
			scan.setFilter(new PageFilter(hm));
		return ht.find(getTablename(bizCode), scan, mapper);
	}

	public List<User> find(String bizCode) {
		Scan scan = HbaseMetaFactory.createScan(null, null, FIELDS);

		return ht.find(getTablename(bizCode), scan, this.mapper);
	}

	
	public List<User> find(String bizCode, String lastRow, int limit) {

		if (limit <= 0)
			return null;
		Filter pageFilter = new PageFilter(limit);
		Scan scan = HbaseMetaFactory.createScan(null, lastRow, null,pageFilter, FIELDS);

		return ht.find(getTablename(bizCode), scan, this.mapper);
	}
	@Override
	protected String getTablename(String bizCode) {
		return bizCode+":"+DEFAULT_MAPPER_TABLE;
	}
	@Override
	protected RowMapper<User> getMapper(){
		return this.mapper;
	}
	
}
