package com.linghua.hdds.api.service;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Service;

import com.linghua.hdds.api.db.HbaseDaoImpl;
import com.linghua.hdds.common.HbaseMetaFactory;
import com.linghua.hdds.meta.Hcolumn;
import com.linghua.hdds.store.Organization;
import com.linghua.hdds.store.User;


@Service
public class OrganizationService  extends ServiceTemplate<Organization>{
	
	private static final String DEFAULT_MAPPER_TABLE="organzation_msg_table";

	
	public Organization mapRow(Result result) throws Exception{
		return this.mapper.mapRow(result, 0);
	}
	
	private RowMapper<Organization> mapper=new RowMapper<Organization>() {

		@Override
		public Organization mapRow(Result result, int rowNum) throws Exception {
			Organization orz = new Organization();
			orz.setId(Bytes.toString(result.getRow()));
			
			for(Entry<Integer, Hcolumn> entry:Organization.HBASE_MAPPING.entrySet()){ 
				HbaseDaoImpl.setField(result, orz, entry);
			}
			
			return orz;
		}
		
	};
		
	@Override
	protected String getTablename(String bizCode) {
		return bizCode+":"+DEFAULT_MAPPER_TABLE;
	}
	@Override
	protected RowMapper<Organization> getMapper(){
		return this.mapper;
	}
}
