package com.linghua.hdds.api.resource;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.linghua.hdds.api.response.OrganizationVo;
import com.linghua.hdds.api.service.OrganizationService;
import com.linghua.hdds.store.Organization;


@RestController
@RequestMapping("/orz")
public class OrganizationController {
	
	@Autowired
	OrganizationService service;
	
	@RequestMapping("/get/{biz}/{oid}")
	@GET
	public Organization get(@PathVariable("biz") String biz,@PathVariable(value="oid") String oid){
				
		return service.get(biz, oid);
	}
	@RequestMapping("/save/{biz}")
	@POST
	public Organization save(@PathVariable("biz") String biz,@RequestBody String orgVo){
		
		OrganizationVo vo=new Gson().fromJson(orgVo, OrganizationVo.class);
		
		Organization orz=new Organization();
		
		if(vo.getDy()!=null){
			Map<String, Float> dy=new HashMap<>();
			for(String t:vo.getDy()){
				dy.put(t, 1.0f);
			}
			orz.setSubscription(dy);
				
		}
		service.delete(biz, vo.getOid(), Organization.HBASE_MAPPING.get(Organization.FIELDS.SUBSCRIPTION.getIndex()));
		service.put(biz, vo.getOid(), orz);
		return null;
	}
	

}
