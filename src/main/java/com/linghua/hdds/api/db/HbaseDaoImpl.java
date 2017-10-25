package com.linghua.hdds.api.db;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.data.hadoop.hbase.HbaseAccessor;
import org.springframework.data.hadoop.hbase.HbaseOperations;
import org.springframework.data.hadoop.hbase.HbaseUtils;
import org.springframework.data.hadoop.hbase.ResultsExtractor;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.data.hadoop.hbase.TableCallback;
import org.springframework.util.Assert;

import com.linghua.hdds.common.ByteUtils;
import com.linghua.hdds.meta.Hcolumn;
import com.linghua.hdds.store.BeanTemplate;


public class HbaseDaoImpl extends HbaseAccessor implements HbaseOperations {

	private Boolean autoFlush = true;

	private HConnection conn = null;

	public HbaseDaoImpl() {
	}

	public HbaseDaoImpl(Configuration conf) {
		setConfiguration(conf);
		afterPropertiesSet();
	}

	public HConnection getConn() throws IOException {
		if (this.conn == null)
			conn = HConnectionManager.createConnection(getConfiguration());

		return conn;
	}

	private HTableInterface getTable(String tableName) {
		try {
			return getConn().getTable(tableName.getBytes(getCharset()));

		} catch (IOException e) {

			e.printStackTrace();
		}

		return null;
	}

	private void releaseTable(String tableName, HTableInterface table) {
		HbaseUtils.releaseTable(tableName, table);

	}

	@Override
	public <T> T execute(String tableName, TableCallback<T> action) {
		Assert.notNull(action, "Callback object must not be null");
		Assert.notNull(tableName, "No table specified");

		HTableInterface table = getTable(tableName);

		try {
			T result = action.doInTable(table);

			return result;
		} catch (Throwable e) {

			if (e instanceof Error)
				throw (Error) e;
			if (e instanceof RuntimeException)
				throw ((RuntimeException) e);

			e.printStackTrace();

			throw HbaseUtils.convertHbaseException((Exception) e);

		} finally {
			releaseTable(tableName, table);

		}

	}

	public Boolean getAutoFlush() {
		return autoFlush;
	}

	public void setAutoFlush(Boolean autoFlush) {
		this.autoFlush = autoFlush;
	}

	public boolean exist(String tableName,String row,String family,String qualifier){
        return execute(tableName, new TableCallback<Boolean>() {
            @Override
            public Boolean doInTable(HTableInterface table) throws Throwable {
                Get get = new Get(row.getBytes());
                get.addColumn(family.getBytes(),qualifier.getBytes());
                return table.exists(get);
            }
        });
    }

    public boolean increments(String tableName, List<Increment> increments){

	   return execute(tableName, new TableCallback<Boolean>() {
            @Override
            public Boolean doInTable(HTableInterface table) throws Throwable {
                try {
                    for (Increment increment : increments)
                        table.increment(increment);
                    return true;
                }catch(IOException e){
                    e.printStackTrace();
                    return false;
                }
            }
        });
    }

	@Override
	public <T> T find(String tableName, String family,ResultsExtractor<T> action) {
		Scan scan = new Scan();
		scan.addFamily(family.getBytes(getCharset()));
		return find(tableName, scan, action);
	}

	@Override
	public <T> T find(String tableName, String family, String qualifier,
			ResultsExtractor<T> action) {
		Scan scan = new Scan();
		scan.addColumn(family.getBytes(getCharset()),
				qualifier.getBytes(getCharset()));
		return find(tableName, scan, action);
	}
	

	@Override
	public <T> T find(String tableName, final Scan scan,
			final ResultsExtractor<T> action) {

		return execute(tableName, new TableCallback<T>() {

			@Override
			public T doInTable(HTableInterface table) throws Throwable {
				ResultScanner scanner = table.getScanner(scan);
				try {
					return action.extractData(scanner);
				} finally {
					scanner.close();
				}
			}
		});
	}

	@Override
	public <T> List<T> find(String tableName, String family,
			final RowMapper<T> action) {
		Scan scan = new Scan();
		scan.addFamily(family.getBytes(getCharset()));
		return find(tableName, scan, action);
	}

	@Override
	public <T> List<T> find(String tableName, String family, String qualifier,
			final RowMapper<T> action) {
		Scan scan = new Scan();
		scan.addColumn(family.getBytes(getCharset()),
				qualifier.getBytes(getCharset()));
		return find(tableName, scan, action);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> find(String tableName, final Scan scan,
			final RowMapper<T> action) {
		return find(tableName, scan, new ResultsExtractor<List<T>>() {
			int rowNum = 0;
			List<T> res = new ArrayList<>();

			@Override
			public List<T> extractData(ResultScanner results) throws Exception {

				for (Result result : results) {
					T e = action.mapRow(result, rowNum++);
					if (e != null)
						res.add(e);
				}

				return res;
			}
		});
	}

	@Override
	public <T> T get(String tableName, String rowName, final RowMapper<T> mapper) {
		return get(tableName, rowName, null, null, mapper);
	}
	
	public <T> List<T> get(String tableName, final List<String> rowNames, final RowMapper<T> mapper) {
		return execute(tableName, new TableCallback<List<T>>() {

			@Override
			public List<T> doInTable(HTableInterface table) throws Throwable {
				List<Get> gets=new ArrayList<Get>();
				for(String rowName:rowNames){
					Get get=new Get(rowName.getBytes());
					gets.add(get);
				}
				Result[] rss=table.get(gets);
				
				if(rss.length!=0){
					List<T> rets=new ArrayList<T>();
					for(Result r:rss)
						rets.add(mapper.mapRow(r, 0));
					return rets;
				}
				return null;
			}
		});
	}

	@Override
	public <T> T get(String tableName, String rowName, String familyName,
			final RowMapper<T> mapper) {
		return get(tableName, rowName, familyName, null, mapper);
	}
	public <T> T get(String tableName, final String rowName, Collection<Hcolumn> hcolumns, final RowMapper<T> mapper){
		return execute(tableName, new TableCallback<T>() {
			@Override
			public T doInTable(HTableInterface table) throws Throwable {
				Get get = new Get(rowName.getBytes(getCharset()));
				if (hcolumns != null) {
					for (Hcolumn col : hcolumns) {
						if (col.getQualifier() != null) {
							get.addColumn(col.getFamily(), col.getQualifier());
						} else {
							get.addFamily(col.getFamily());
						}
					}
				}
				return mapper.mapRow(table.get(get), 0);
			}
	});
	}
	@Override
	public <T> T get(String tableName, final String rowName,
			final String familyName, final String qualifier,
			final RowMapper<T> mapper) {
		return execute(tableName, new TableCallback<T>() {
			@Override
			public T doInTable(HTableInterface htable) throws Throwable {
				Get get = new Get(rowName.getBytes(getCharset()));
				if (familyName != null) {
					byte[] family = familyName.getBytes(getCharset());

					if (qualifier != null) {
						get.addColumn(family, qualifier.getBytes(getCharset()));
					} else {
						get.addFamily(family);
					}
				}
				return mapper.mapRow(htable.get(get), 0);
			}
		});
	}

	@Override
	public void put(String tableName, final String rowName,
			final String familyName, final String qualifier, final byte[] value) {
		Assert.hasLength(rowName);
		Assert.hasLength(familyName);
		Assert.hasLength(qualifier);
		Assert.notNull(value);
		execute(tableName, new TableCallback<Object>() {
			@Override
			public Object doInTable(HTableInterface htable) throws Throwable {
				Put put = new Put(rowName.getBytes(getCharset())).add(
						familyName.getBytes(getCharset()),
						qualifier.getBytes(getCharset()), value);
				htable.put(put);
				return null;
			}
		});
	}

	public <T> void put(String tableName, final Put put) {
		execute(tableName, new TableCallback<T>() {

			@Override
			public T doInTable(HTableInterface table) throws Throwable {
				table.put(put);
				return null;
			}
		});
	}
	

	public <T> void put(String tableName, final List<Put> puts) {
		execute(tableName, new TableCallback<T>() {

			@Override
			public T doInTable(HTableInterface table) throws Throwable {
				table.put(puts);
				return null;
			}
		});
	}
	
	public void delete(String tableName,final String[] rowNames){
		

		Assert.notEmpty(rowNames);
		execute(tableName, new TableCallback<Object>() {
			@Override
			public Object doInTable(HTableInterface htable) throws Throwable {
				List<Delete> deletes=new ArrayList<>();
				for(String rowName:rowNames){
					Delete delete = new Delete(rowName.getBytes(getCharset()));
					deletes.add(delete);

				}
				htable.delete(deletes);
				return null;
			}
		});

	}

	@Override
	public void delete(String tableName, final String rowName,
			final String familyName) {
		delete(tableName, rowName, familyName, null);
	}

	@Override
	public void delete(String tableName, final String rowName,
			final String familyName, final String qualifier) {
		Assert.hasLength(rowName);
		Assert.hasLength(familyName);
		execute(tableName, new TableCallback<Object>() {
			@Override
			public Object doInTable(HTableInterface htable) throws Throwable {
				Delete delete = new Delete(rowName.getBytes(getCharset()));
				byte[] family = familyName.getBytes(getCharset());

				if (qualifier != null) {
					delete.deleteColumn(family,
							qualifier.getBytes(getCharset()));
				} else {
					delete.deleteFamily(family);
				}

				htable.delete(delete);
				return null;
			}
		});
	}

	public long incrementColumnValue(String tableName,final byte[] row,final byte[] family,final byte[] qualifier,final long amount){
		
		return execute(tableName, new TableCallback<Long>() {

			@Override
			public Long doInTable(HTableInterface table)
					throws Throwable {
				// TODO Auto-generated method stub
				return table.incrementColumnValue(row, family, qualifier, amount);
			}
		});
		
	}
	
	
	public static void addPutsAndDeletes(Put put, Delete delete, Object o,
			Hcolumn col) {

		byte[] qualifier = col.getQualifier();
		Class<?> type = o.getClass();

		addPutsAndDeletes(put, delete, o, col, qualifier);

	}

	public static void addPutsAndDeletes(Put put, Delete delete, Object o,
			Hcolumn col, byte[] qualifier) {

		if (o instanceof Map) {
			// if(qualifier==null)
			// dele
			Set<Entry> set = ((Map) o).entrySet();
			for (Entry e : set) {
				qualifier = ByteUtils.toBytes(e.getKey());
				addPutsAndDeletes(put, delete, e.getValue(), col, qualifier);
			}
		} else if (o instanceof List) {
			List<?> array = (List<?>) o;
			int j = 0;

			for (Object e : array) {
				addPutsAndDeletes(put, delete, e, col, Bytes.toBytes(j++));
			}
		} else {
			put.add(col.getFamily(), qualifier, ByteUtils.toBytes(o));

		}

	}

	public static void setField(Result result, BeanTemplate bean, int pos,
			Hcolumn col) {

		if (col.getQualifier() == null) {
			NavigableMap<byte[], byte[]> maps = result.getFamilyMap(col
					.getFamily());
			if (maps == null||maps.isEmpty())
				return;
			else {
				Map<CharSequence, Object> o = new HashMap<CharSequence, Object>();
				for (Entry<byte[], byte[]> kv : maps.entrySet()) {
					o.put(Bytes.toString(kv.getKey()),
							ByteUtils.fromBytes(kv.getValue(), col.getType()));

				}
//				System.out.println(new String(col.getFamily())+"::pos"+pos);
//				System.out.println(o.size());
				bean.put(pos, o);
			}
		} else {
			byte[] val = result.getValue(col.getFamily(), col.getQualifier());
			if (val == null)
				return;
			Object o = ByteUtils.fromBytes(val, col.getType());
			bean.put(pos, o);
		}

	}

	public static void setField(Result result, BeanTemplate bean,
			Entry<Integer, Hcolumn> entry) {
		setField(result, bean, entry.getKey(), entry.getValue());

	}

}
