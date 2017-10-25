package com.linghua.hdds.preference.model;

import com.google.gson.stream.JsonReader;
import com.linghua.hdds.meta.TwoTuple;
import com.linghua.hdds.store.Item;
import com.linghua.hdds.store.User;
import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.*;
import java.util.*;


public class BaseTagWithLabelRecommendModel {

	private static BaseTagWithLabelRecommendModel model;
	private Map<String, Integer> indexStringModel;
	private int tagLength =0;

	private Map<String,Map<String, SparseVector>> mtrix;
	
	public static BaseTagWithLabelRecommendModel getInstance(String stopRow){
		if(model==null)
			model=new BaseTagWithLabelRecommendModel(null, stopRow);
			
		return model;
	}

	public static void reload(){
        model = null;
    }

	private BaseTagWithLabelRecommendModel(Map<String, Integer> indexStringModel,String stopRow) {

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
        this.tagLength = indexStringModel.size();
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

				scan.addColumn("f".getBytes(),"lb".getBytes());
				ResultScanner rs=table.getScanner(scan);
				Iterator<Result> rit=rs.iterator();
				while(rit.hasNext()){
					Result r=rit.next();
					Map<Integer, Float> kw=new HashMap<>();
					String label=null;
					for(Cell cell:r.listCells()){
					    String q=Bytes.toString(CellUtil.cloneQualifier(cell));
					    if(q.equals("lb")){
					        label=Bytes.toString(CellUtil.cloneValue(cell));
					        continue;
					    }
					    Object idx=indexStringModel.get(q);
					    if(idx!=null){
					    	kw.put((int)idx, Bytes.toFloat(CellUtil.cloneValue(cell)));
					    }
					}
					if(kw.size()!=0) {
						Map<String,SparseVector> vectorMap= mtrix.getOrDefault(label,new HashMap<String,SparseVector>());
                        vectorMap.put(Bytes.toString(r.getRow()), SparseVector.toSparseVector(kw));
                        mtrix.put(label,vectorMap);
					}
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

//	public List<TwoTuple<String, Double>> recommendByItem(Item i) {
//		Map<Integer, Double> vector = new HashMap<>();
//		i.getKeyword().forEach((l, r) -> vector.put(indexStringModel.get(l), Double.valueOf(r)));
//		DenseVector iv = DenseVector.toDenseVector(indexStringModel.size(), vector);
//
//		List<TwoTuple<String, Double>> result = new ArrayList<>();
//		long begin=System.currentTimeMillis();
//		mtrix.entrySet().parallelStream().forEach(entry -> {
//			double y = iv.axpy(1, 1, entry.getValue());
//			if (y > 0) {
//				result.add(new TwoTuple<String, Double>(entry.getKey(), y));
//			}
//
//		});
//		System.out.println("time............."+(System.currentTimeMillis()-begin));
//		return result;
//	}

	public List<TwoTuple<String, Double>> recommend(String[] keys,List<String> filter) {
        Map<Integer, Double> vector = new HashMap<>();
	    for(String k:keys){
	        int index = indexStringModel.getOrDefault(k,-1);
	        if(index != -1)
	           vector.put(indexStringModel.get(k),1.0);
        }
        DenseVector vec =DenseVector.toDenseVector(tagLength,vector);

		return axpy(vec,filter);
	}

	private List<TwoTuple<String, Double>> axpy(DenseVector vec,Collection<String> filter){
        long begin = System.currentTimeMillis();
        final double a= vec.values ==null? 0:1;

			List<TwoTuple<String, Double>> result =new ArrayList<>();
			if(filter ==null || filter.size() ==0) {
                mtrix.values().forEach(entry -> {
                    entry.entrySet().parallelStream().forEach( kv -> {
                        double y = vec.axpy(a, 1, kv.getValue());
                        if (y > 0) {
                            result.add(new TwoTuple<String, Double>(kv.getKey(), y));
                        }
                    });
                });
            } else {
			    for (String label : filter) {
				if (mtrix.containsKey(label)) {
					mtrix.get(label).entrySet().parallelStream().forEach(entry -> {
						double y = vec.axpy(a, 1, entry.getValue());
						if (y > 0) {
							result.add(new TwoTuple<String, Double>(entry.getKey(), y));
						}

					});

				}
			}}
        System.out.println("compute time is :"+(System.currentTimeMillis()- begin));
        return result;

	}

	public List<TwoTuple<String, Double>> recommend(User u) {
		Map<Integer, Double> vector = new HashMap<>();
        Collection<String> sub = u.getSubscription()==null ? null:u.getSubscription().keySet();

        if(u.getGraph()!=null) {
            u.getGraph().forEach((l, r) -> vector.put(indexStringModel.getOrDefault(l, 0), Double.valueOf(r)));
            DenseVector uv = DenseVector.toDenseVector(tagLength, vector);
            return axpy(uv,sub);
        }else {

                DenseVector uv = new DenseVector();
            return axpy(uv,sub);
        }
	}

    public List<TwoTuple<String, Double>> getByCatalog(String label,int starRow,int hm) {
        List<TwoTuple<String, Double>> result =new ArrayList<>();
        int i=0;
        System.out.println(label);
        Set<String> ids = mtrix.get(label).keySet();
        System.out.println("number of item may be find alll:"+ids.size());
        for(String k:ids){
            if(i < starRow){
                i++;
                continue;
            }
            if(result.size() >hm)
                return result;
            result.add(new TwoTuple<>(k,1.0d));
        }

	    return result;
    }
	
	
	public static void main(String[] args) {
		
		Map<String, Integer> a=new HashMap<>();
		a.put("a", 12);
		
		Object j=a.get("a");
		System.out.println((int)j);
		
	}

}