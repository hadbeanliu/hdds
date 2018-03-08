package com.linghua.hdds.api.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;

import com.google.common.reflect.TypeToken;
import com.linghua.hdds.api.response.Node;
import com.linghua.hdds.common.CataLogManager;
import com.linghua.hdds.common.TableUtil;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.linghua.hdds.api.response.UserVo;
import com.linghua.hdds.api.service.UserService;
import com.linghua.hdds.store.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value="/user")
public class UserController {
	@Autowired
	private UserService dao;
		
	@RequestMapping(value="/save/{biz}/{uid}")
	public String saveOrUpdate(@PathVariable("biz") String biz,@PathVariable("uid")String uid, @RequestBody UserVo vo){
		
		Assert.notNull(uid, "id must be specified !");
		Assert.notNull(biz, "biz_code must be specified !");
		
		if(vo == null)
			return "fail";

		User u=new User();
		
		vo.exchange(u);
        System.out.println(new Gson().toJson(vo));
        System.out.println(new Gson().toJson(u));
        String row = TableUtil.idReverseAndBuild(uid);
        if(u.getSubscription()!=null)
			this.dao.delete(biz, row, User.HBASE_MAPPING.get(User.FIELDS.SUBSCRIPTION.getIndex()));
        this.dao.put(biz, row, u);

		return "success";
	}
	
	@RequestMapping(value="/delete/headlines/")
	public String delete(HttpServletRequest req){
				
		return null;
	}
    @RequestMapping(value="/reflesh/{code}/{uid}")
	public String reflesh(@PathVariable(value="code")String code,@PathVariable(value="uid")String uid){

	    if(code ==null){
	        return "bad request";
        }

        if(code.equals("reflesh_all_with_cms_reflesh")){
	        if(uid!=null){
                User u = this.dao.get("headlines", TableUtil.idReverseAndBuild(uid));

                refleshUser(u,uid);
            }
        }else if(code.equals("reflesh_all_with_cms_reflesh_all")){
                List<User> us =dao.find("headlines");
                for(User u:us){
                    refleshUser(u,uid);

            }
        }

	    return null;

    }
    private String refleshUser(User u,String uid){
        if(u.getSubscription()==null){
            return "success";
        }else {
            if(u.getInfo().get("config")!=null) {
                List<Node> nodes = new Gson().fromJson((String)u.getInfo().get("config"),new TypeToken<List<Node>>(){}.getType());
                for(Node site : nodes){
                    String siteId=null;
                    if(site.getCaId().equals("190014")) {
                        siteId = "headlines";
                        site.setCaName("新闻");
                    }else if(site.getCaId().equals("190019")){
                        siteId="govheadlines";
                        site.setCaName("政务");
                    }else{
                        siteId=site.getCaId();
                    }

                    List<Node> toReomve=new ArrayList<>();
                    for(Node ca:site.getChildren()){
                        if (CataLogManager.getAllCatalog(siteId).get(ca.getCaName()) ==null){
                            toReomve.add(ca);
                        }
                    }
                    if(toReomve.size()>0){
                        System.out.println(siteId);
                        for(Node node :toReomve){
                            System.out.println(node.getCaName()+":"+site.getChildren().remove(node));
                        }
                    }
                }
                System.out.println(nodes);
                u.setSubscription(null);
                UserVo vo =new UserVo();
                vo.setConfig(nodes);
                vo.exchange(u);
                System.out.println(new Gson().toJson(nodes));

                System.out.println(new Gson().toJson(u));
                this.dao.delete("headlines", TableUtil.idReverseAndBuild(uid), User.HBASE_MAPPING.get(User.FIELDS.SUBSCRIPTION.getIndex()));
                this.dao.put("headlines", TableUtil.idReverseAndBuild(uid), u);
            }
        }
        return null;
    }
	
	@RequestMapping(value="/del/{biz_code}/{uid}")
	public User del(@PathVariable("uid") String uid,@PathVariable("biz_code") String biz_code){

		return null;
	}
	
	@RequestMapping(value="/get/{biz_code}/{uid}")
	public User get(@PathVariable("uid") String uid,@PathVariable("biz_code") String biz_code){
        System.out.println(biz_code+"---"+uid);
        User u = this.dao.get(biz_code, TableUtil.idReverseAndBuild(uid));
        u.setId(uid);
        System.out.println(new Gson().toJson(u));
        return u;
	}

	@RequestMapping(value = "/get/graph/{biz_code}/{uid}")
    @ResponseBody
    public Map<String,Long> getUserGraph(@PathVariable("uid") String uid, @PathVariable("biz_code") String biz_code){
        Assert.hasLength(uid,"不合法的用户");

        User u = this.dao.get(biz_code, uid);
        if( u!=null && u.getInfo()!=null && u.getInfo().get("config")!=null)
            return u.getGraph();
        return null;
    }

	@RequestMapping(value="/getConfig/{biz_code}/{uid}/{pid}")
	@ResponseBody
	public List<Node> getUserConfig(@PathVariable("uid") String uid, @PathVariable("biz_code") String biz_code, @PathVariable("pid") String pid){
		Assert.hasLength(pid,"不合法的用户");
		if(pid.equals("all")){
            return CataLogManager.getCatalogTree(pid);
        }else pid = TableUtil.idReverseAndBuild(pid);

		User u = this.dao.get(biz_code, pid);
		if( u!=null && u.getInfo()!=null && u.getInfo().get("config")!=null)
		    return new Gson().fromJson((String)u.getInfo().get("config"),new TypeToken<List<Node>>(){}.getType());
		return null;
	}

	@RequestMapping(value="/find")
	@ResponseBody
	public String find(@QueryParam("biz_code") String biz_code,@QueryParam("lr") String lr,@QueryParam("limit") int limit){
		System.out.println("limit="+limit+"   lr="+lr);
		return new Gson().toJson(dao.find(biz_code,lr, limit));
	}

	
}
