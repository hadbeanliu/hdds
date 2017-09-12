package com.linghua.hdds.api.response;

import java.util.List;
import java.util.Map;

public class ItemVo {
	
	
	private List<String> tag;
	
	private String siteId;
	
	private String caId;
	
	private String id;
	
	private String catagory;
	
	private float manualScore;
	
	private String pubdate;
	
	private String title;
	
	private String bizCode;
	
	private Map<String, String> sys;
	
	private String ns;
	
	private String nt;
	
	private String nr;



	public List<String> getTag() {
		return tag;
	}

	public void setTag(List<String> tag) {
		this.tag = tag;
	}

	public String getCaId() {
		return caId;
	}

	public void setCaId(String caId) {
		this.caId = caId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCatagory() {
		return catagory;
	}

	public void setCatagory(String catagory) {
		this.catagory = catagory;
	}

	public float getManualScore() {
		return manualScore;
	}

	public void setManualScore(float manualScore) {
		this.manualScore = manualScore;
	}

	public String getPubdate() {
		return pubdate;
	}

	public void setPubdate(String pubdate) {
		this.pubdate = pubdate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Map<String, String> getSys() {
		return sys;
	}

	public void setSys(Map<String, String> sys) {
		this.sys = sys;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getBizCode() {
		return bizCode;
	}

	public void setBizCode(String bizCode) {
		this.bizCode = bizCode;
	}

	public String getNs() {
		return ns;
	}

	public void setNs(String ns) {
		this.ns = ns;
	}

	public String getNt() {
		return nt;
	}

	public void setNt(String nt) {
		this.nt = nt;
	}

	public String getNr() {
		return nr;
	}

	public void setNr(String nr) {
		this.nr = nr;
	}
	
	
	

}
