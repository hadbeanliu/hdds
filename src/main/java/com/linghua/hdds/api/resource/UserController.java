package com.linghua.hdds.api.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;

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


@RestController
@RequestMapping(value="/user")
public class UserController {
	@Autowired
	private UserService dao;
		
	@RequestMapping(value="/save/{biz}/{uid}")
	@ResponseBody
	public String saveOrUpdate(@PathVariable("biz") String biz,@PathVariable("uid")String uid, @RequestBody UserVo vo){
		
		System.out.println(biz+"---"+uid);
		Assert.notNull(uid, "id must be specified !");
		Assert.notNull(biz, "biz_code must be specified !");
		
		if(vo == null)
			return "fail";
		
//		dao.put(biz,uid,user);
		
		User u=new User();
		
		vo.exchange(u);
		
		if(u.getSubscription()!=null)
			this.dao.delete(biz, u.getId(), User.HBASE_MAPPING.get(User.FIELDS.SUBSCRIPTION.getIndex()));
		System.out.println(new Gson().toJson(u));
		this.dao.put(biz, u.getId(), u);
		
		return "success";
	}
	
	@RequestMapping(value="/delete")
	@ResponseBody
	public String delete(HttpServletRequest req){
				
		return null;
	}	
	
	@RequestMapping(value="/del/{biz_code}/{uid}")
	@ResponseBody
	public User del(@PathVariable("uid") String uid,@PathVariable("biz_code") String biz_code){
//		System.out.println("/user/get?uid="+uid+"&biz_code="+biz_code);
		
		return null;
	}
	
	@RequestMapping(value="/get/{biz_code}/{uid}")
	@ResponseBody
	public User get(@PathVariable("uid") String uid,@PathVariable("biz_code") String biz_code){
//		System.out.println("/user/get?uid="+uid+"&biz_code="+biz_code);

		return this.dao.get(biz_code, uid);
	}

	@RequestMapping(value="/find")
	@ResponseBody
	public String find(@QueryParam("biz_code") String biz_code,@QueryParam("lr") String lr,@QueryParam("limit") int limit){
		System.out.println("limit="+limit+"   lr="+lr);
		return new Gson().toJson(dao.find(biz_code,lr, limit));
	}

	
}
