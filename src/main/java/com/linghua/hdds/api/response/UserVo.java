package com.linghua.hdds.api.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.linghua.hdds.store.User;

public class UserVo implements VoExchange<String, User> {

	private String uid;

	private String location;

	private String oid;

	private List<String> graph;

	private List<String> sub;

	private String name;

	private String age;

	private String gender;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public List<String> getGraph() {
		return graph;
	}

	public void setGraph(List<String> graph) {
		this.graph = graph;
	}

	public List<String> getSub() {
		return sub;
	}

	public void setSub(List<String> sub) {
		this.sub = sub;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	@Override
	public void exchange(User u) {
		
		u.setId(this.uid);
		Map<String,Object> info=new HashMap();
		if (this.getAge() != null)
			info.put("age", this.getAge());
		if (this.getGender() != null)
			info.put("sex", this.getGender());
		if (this.getLocation() != null)
			info.put("wz", this.getLocation());
		if (this.getName() != null)
			info.put("name", this.getName());
		info.put("oid", this.getOid());
		u.setInfo(info);
		if(this.getGraph()!=null&&this.getGraph().size()>0){
			Map<String,Long> graph=new HashMap<>(); 
			this.getGraph().forEach(key->graph.put(key, (long) 1));
			u.setGraph(graph);
		}
		if(this.sub!=null&&this.sub.size()>0){
			Map<String, Float> sub=new HashMap<>();
			this.getSub().forEach(x->sub.put(x, 1.0f));
			u.setSubscription(sub);
		}
	}
	
	
}
