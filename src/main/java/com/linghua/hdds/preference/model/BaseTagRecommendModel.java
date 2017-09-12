package com.linghua.hdds.preference.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.google.gson.stream.JsonReader;
import com.linghua.hdds.meta.TwoTuple;
import com.linghua.hdds.store.Item;
import com.linghua.hdds.store.User;


public class BaseTagRecommendModel {

	private static BaseTagRecommendModel model;
	private Map<String, Integer> indexStringModel;

	private Map<String, SparseVector> mtrix;
	
	public static BaseTagRecommendModel getInstance(String stopRow){
		if(model==null)
			model=new BaseTagRecommendModel(null, stopRow);
			
		return model;
	}

	private BaseTagRecommendModel(Map<String, Integer> indexStringModel,String stopRow) {

		if (indexStringModel == null) {

			File f = new File("/home/hadoop/result/tags.json");
			indexStringModel=new HashMap<>();
			
			try {
				InputStreamReader read=new InputStreamReader(new FileInputStream(f), "utf-8");
//				read.read(content);
				JsonReader jreader=new JsonReader(read);
				jreader.setLenient(true);
				jreader.beginObject();
				
				while(jreader.hasNext()){

					indexStringModel.put(jreader.nextName(), jreader.nextInt());
				}
				jreader.endObject();	
				jreader.close();
				read.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				
				
			}
			
		}

		this.indexStringModel = indexStringModel;
		
		
		if(mtrix==null){
			mtrix=new HashMap<>();
			Connection conn=null;
			Table table=null;
			try {
				conn=ConnectionFactory.createConnection();
				
				table=conn.getTable(TableName.valueOf( "headlines:item_meta_table"));
				
				Scan scan=new Scan();
				scan.setStopRow(stopRow.getBytes());
				scan.addFamily("kw".getBytes());
				ResultScanner rs=table.getScanner(scan);
				Iterator<Result> rit=rs.iterator();
				while(rit.hasNext()){
					Result r=rit.next();
					Map<Integer, Float> kw=new HashMap<>();
					for(Cell cell:r.listCells()){
					    String q=Bytes.toString(CellUtil.cloneQualifier(cell));
					    Object idx=indexStringModel.get(q);
					    if(idx!=null){
					    	kw.put((int)idx, Bytes.toFloat(CellUtil.cloneValue(cell)));
					    }
					}
					if(kw.size()!=0)
						mtrix.put(Bytes.toString(r.getRow()),SparseVector.toSparseVector(kw));
				}
				
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}finally{
				try {
					table.close();
					conn.close();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				
				
			}
			
			
		}
		
		System.out.println("length of item to recommend:::"+mtrix.size());
		
	}

	public List<TwoTuple<String, Double>> recommendByItem(Item i) {
		Map<Integer, Double> vector = new HashMap<>();
		i.getKeyword().forEach((l, r) -> vector.put(indexStringModel.get(l), Double.valueOf(r)));
		DenseVector iv = DenseVector.toDenseVector(indexStringModel.size(), vector);

		List<TwoTuple<String, Double>> result = new ArrayList<>();
		long begin=System.currentTimeMillis();
		mtrix.entrySet().parallelStream().forEach(entry -> {
			double y = iv.axpy(1, 1, entry.getValue());
			if (y > 0) {
				result.add(new TwoTuple<String, Double>(entry.getKey(), y));
			}

		});
		System.out.println("time............."+(System.currentTimeMillis()-begin));
		return result;
	}

	
	public List<TwoTuple<String, Double>> recommend(User u) {
		Map<Integer, Double> vector = new HashMap<>();
		u.getGraph().forEach((l, r) -> vector.put(indexStringModel.getOrDefault(l, 0), Double.valueOf(r)));
		DenseVector uv = DenseVector.toDenseVector(indexStringModel.size(), vector);

		List<TwoTuple<String, Double>> result = new ArrayList<>();
		long begin=System.currentTimeMillis();
		mtrix.entrySet().parallelStream().forEach(entry -> {
			double y = uv.axpy(1, 1, entry.getValue());
			if (y > 0) {
				result.add(new TwoTuple<String, Double>(entry.getKey(), y));
			}

		});
		System.out.println("time............."+(System.currentTimeMillis()-begin));
		return result;
	}
	
	
	public static void main(String[] args) {
		
		Map<String, Integer> a=new HashMap<>();
		a.put("a", 12);
		
		Object j=a.get("a");
		System.out.println((int)j);
		
	}

}
class SparseVector {

	private static SparseVector ZERO = new SparseVector(0, null, null);

	public int nnz;
	public int[] indics;
	public double[] values;

	public SparseVector() {
	}

	private SparseVector(int nnz, int[] indics, double[] values) {
		this.nnz = nnz;
		if (nnz == 0) {
			indics = new int[0];
			values = new double[0];
		}
	}

	public static SparseVector toSparseVector(Map<Integer, Float> vector) {

		if (vector == null || vector.size() == 0)
			return ZERO;
		SparseVector sparse = new SparseVector();
		sparse.nnz = vector.size();
		sparse.indics = new int[sparse.nnz];
		sparse.values = new double[sparse.nnz];
		int i = 0;
		for (Entry<Integer, Float> entry : vector.entrySet()) {
			sparse.indics[i] = entry.getKey();
			sparse.values[i] = entry.getValue();
			i++;
		}

		return sparse;
	}

	// y := alpha*A +beta*Y
	private double axpy(double a, double b, SparseVector y) {

		return 0;
	}

}

class DenseVector {

	double[] values;

	public static DenseVector toDenseVector(int length, Map<Integer, Double> vector) {

		int size = vector.size();
		if (size <= length) {
			DenseVector dense = new DenseVector();
			dense.values = new double[length];
			vector.forEach((l, r) -> dense.values[l] = r);
			return dense;
		}

		DenseVector dense = new DenseVector();
		dense.values = new double[length];
		vector.forEach((l, r) -> {
			if (l < length)
				dense.values[l] = r;
		});
		return dense;

	}

	public double axpy(double a, double b, SparseVector y) {
		if (a == 0 && b == 0) {

			return 0;
		} else if (a == 0) {

			return b;
		} else if (b == 0) {

			return a;
		}

		int yLength = y.indics.length;
		int[] yIndics = y.indics;
		double[] yValues = y.values;

		int i = 0;
		double r = 0;
		while (i < yLength) {
			r += values[yIndics[i]] * yValues[i];
			i+=1;
		}

		return r;
	}

}