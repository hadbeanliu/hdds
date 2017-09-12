package com.linghua.hdds.api.response;

import java.util.List;

public class OrganizationVo {

	private String oid;
	private List<String> dy;
	
	private String location;
	
	private List<String> kw;
//	private 

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public List<String> getDy() {
		return dy;
	}

	public void setDy(List<String> dy) {
		this.dy = dy;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<String> getKw() {
		return kw;
	}

	public void setKw(List<String> kw) {
		this.kw = kw;
	}
	
	
	
	
}
