package com.linghua.hdds.api.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.linghua.hdds.store.User;

public class UserVo implements VoExchange<String, User> {

	private String uid;

	private String location;

	private String pid;

	private List<String> graph;

	private List<String> sub;

	private String name;

	private String age;

	private String gender;

	private List<Node> config;

	private Map<String,String> keyConfig;

	private List<String> placeSub;

    public List<Node> getConfig() {
        return config;
    }

    public void setConfig(List<Node> config) {
        this.config = config;
    }

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


    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
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

    public Map<String, String> getKeyConfig() {
        return keyConfig;
    }

    public void setKeyConfig(Map<String, String> keyConfig) {
        this.keyConfig = keyConfig;
    }

    public List<String> getPlaceSub() {
        return placeSub;
    }

    public void setPlaceSub(List<String> placeSub) {
        this.placeSub = placeSub;
    }

    @Override
	public void exchange(User u) {
		
		u.setId(this.uid);
		Map<String,Object> info=new HashMap();
        info.put("lmdf",System.currentTimeMillis()+"");
        if (this.getAge() != null)
			info.put("age", this.getAge());
		if (this.getGender() != null)
			info.put("sex", this.getGender());
		if (this.getLocation() != null)
			info.put("wz", this.getLocation());
		if (this.getName() != null)
			info.put("name", this.getName());
		if(this.getPid() !=null){
		    info.put("pid",this.getPid());
        }
		u.setInfo(info);
		if(this.getGraph()!=null&&this.getGraph().size()>0){
			Map<String,Long> graph=new HashMap<>(); 
			this.getGraph().forEach(key->graph.put(key, (long) 1));
			u.setGraph(graph);
		}
		if(this.getConfig()!=null){
		    u.getInfo().put("config",new Gson().toJson(this.getConfig()));
		    Map<String,Float> uSub=new HashMap<>();
		    for(Node node :this.getConfig()){
                if(node.hasChldren())
                    getAllNode(node,uSub);
            }
            u.setSubscription(uSub);
        }
        if(this.getPlaceSub()!=null){
            u.getInfo().put("placeConfig",new Gson().toJson(this.getPlaceSub()));
        }
        if(this.getKeyConfig() !=null){
            Map<String,Long> graph=new HashMap<>();
            for(String key:this.getKeyConfig().keySet()){
            }
            info.put("keyConfig",new Gson().toJson(this.getKeyConfig()));
        }

		if(this.sub!=null&&this.sub.size()>0){
			Map<String, Float> uSub=new HashMap<>();
			this.getSub().forEach(x->uSub.put(x, 1.0f));
			if(u.getSubscription() ==null)
			    u.setSubscription(uSub);
			else u.getSubscription().putAll(uSub);
		}
	}

	private void getAllNode(Node node,Map<String,Float> sub){
        if(node.hasChldren()){
            for(Node child :node.getChildren())
                getAllNode(child,sub);
        }
        sub.put(node.getCaName(),1.0f);
    }

	@Override
	public VoExchange to(User obj) {
		return null;
	}


}