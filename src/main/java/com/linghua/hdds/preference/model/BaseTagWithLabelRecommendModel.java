package com.linghua.hdds.preference.model;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
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
    private static final String tagsIndexPath = "/home/hadoop/result/tagsIndex.model";

	private static BaseTagWithLabelRecommendModel model;
	private static Map<String,BaseTagWithLabelRecommendModel> models=new HashMap<>();
	private Map<String, Integer> indexStringModel;
	private int tagLength =0;

	private Map<String,Map<String, SparseVector>> mtrix;
	
	public static BaseTagWithLabelRecommendModel getInstance(String stopRow){
		return getInstance("headlines",stopRow);
	}

	public static BaseTagWithLabelRecommendModel getInstance(String biz,String stopRow){
	    if(models.get(biz)!=null){
	        return models.get(biz);
        }else {
	        models.put(biz,new BaseTagWithLabelRecommendModel(biz,stopRow));
        }
		return models.get(biz);
	}

	private void load(String path){
        String tmp ="";
        try {
            BufferedReader read =new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));

            int cnt=0;
            while((tmp=read.readLine())!=null){
                String[] kv = tmp.split("/004");
                cnt++;
                indexStringModel.put(kv[0],Integer.valueOf(kv[1]));
            }
            System.out.println("cnt:"+cnt);
            read.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("tmp...."+tmp);
        }
    }

	public static void reload(){
        model = null;
    }

	private BaseTagWithLabelRecommendModel(String biz,String stopRow) {

		if (this.indexStringModel == null) {
            this.indexStringModel=new HashMap<>();
			load(tagsIndexPath);
		}

        this.tagLength = indexStringModel.size();
        System.out.println(tagLength);
        if(mtrix==null){
			mtrix=new HashMap<>();
			Connection conn=null;
			Table table=null;
			try {
				conn=ConnectionFactory.createConnection();
				Gson gson =new Gson();
				table=conn.getTable(TableName.valueOf( biz+":item_meta_table"));
				
				Scan scan=new Scan();
				scan.setStopRow(stopRow.getBytes());
				final byte[] sys= "sys".getBytes();
				final byte[] tags ="tags".getBytes();
				scan.addColumn("sys".getBytes(),"tags".getBytes());

				scan.addColumn("f".getBytes(),"lb".getBytes());
				ResultScanner rs=table.getScanner(scan);
				Iterator<Result> rit=rs.iterator();
				Random random=new Random();
				while(rit.hasNext()){
					Result r=rit.next();
					Map<Integer, Float> kw=new HashMap<>();
					String label=null;

					if(r.isEmpty()||r.size()!=2){
					    continue;
                    }
                    Map <String,Float> t= gson.fromJson(Bytes.toString(r.getValue(sys,tags)),new TypeToken<Map<String,Float>>(){}.getType());
                    for(Map.Entry<String,Float> kv:t.entrySet()){
                        kw.put(indexStringModel.getOrDefault(kv.getKey(),random.nextInt(tagLength)),kv.getValue());
                    }

                    label = Bytes.toString(r.getValue("f".getBytes(),"lb".getBytes()));
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

	public List<TwoTuple<String, Float>> recommend(Map<String,Float> keys,List<String> filter) {
        Map<Integer, Float> vector = new HashMap<>();
	    for(String k:keys.keySet()){
	        int index = indexStringModel.getOrDefault(k,-1);
	        if(index != -1)
	           vector.put(indexStringModel.get(k),keys.get(k));
        }
        DenseVector vec =DenseVector.toDenseVector(tagLength,vector);

		return axpy(vec,filter);
	}

	private List<TwoTuple<String, Float>> axpy(DenseVector vec,Collection<String> filter){
        long begin = System.currentTimeMillis();
        final float a= vec.values ==null? 0:1;

			List<TwoTuple<String, Float>> result =new ArrayList<>();
			if(filter ==null || filter.size() ==0) {
                mtrix.values().forEach(entry -> {
                    entry.entrySet().parallelStream().forEach( kv -> {
                        float y = vec.axpy(a, 1, kv.getValue());
                        if (y > 0) {
                            result.add(new TwoTuple<String, Float>(kv.getKey(), y));
                        }
                    });
                });
            } else {
				for (String label : filter) {
				if (mtrix.containsKey(label)) {
					mtrix.get(label).entrySet().parallelStream().forEach(entry -> {
						float y = vec.axpy(a, 1, entry.getValue());
						if (y > 0) {
							result.add(new TwoTuple<String, Float>(entry.getKey(), y));
						}

					});

				}
			}}
        System.out.println("compute time is :"+(System.currentTimeMillis()- begin));
        return result;

	}

	public List<TwoTuple<String, Float>> recommend(User u) {
		Map<Integer, Float> vector = new HashMap<>();
        Collection<String> sub = u.getSubscription()==null ? null:u.getSubscription().keySet();

        if(u.getGraph()!=null) {
            u.getGraph().forEach((l, r) -> vector.put(indexStringModel.getOrDefault(l, 0), Float.valueOf(r)));
            DenseVector uv = DenseVector.toDenseVector(tagLength, vector);
            return axpy(uv,sub);
        }else {

                DenseVector uv = new DenseVector();
            return axpy(uv,sub);
        }
	}

    public List<TwoTuple<String, Float>> getByCatalog(String label,int starRow,int hm) {
        List<TwoTuple<String, Float>> result =new ArrayList<>();
        int i=0;
        System.out.println(label);
        Set<String> ids = mtrix.get(label).keySet();
        System.out.println("number of item may be find all:"+ids.size());
        for(String k:ids){
            if(i < starRow){
                i++;
                continue;
            }
            if(result.size() >hm)
                return result;
            result.add(new TwoTuple<String, Float>(k,1.0f));
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