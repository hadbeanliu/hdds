package com.linghua.hdds.api.response;

import com.linghua.hdds.common.DateUtils;
import com.linghua.hdds.common.MainWordExtractor;
import com.linghua.hdds.common.TableUtil;
import com.linghua.hdds.store.Item;
import org.lionsoul.jcseg.tokenizer.core.JcsegException;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ItemVo implements VoExchange<String,Item>{
	
	
	private String[] tag;
	
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

	private String np;

	private String[] nsTag;

	private String[] ntTag;

	private String[] nrTag;

	private String[] npTag;

	private double score;

	private String text;

	private String author;

	private String createTime;

	private String pubdateTime;

	private String redirectUrl;

	private String recTag;

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getPubdateTime() {
		return pubdateTime;
	}

	public void setPubdateTime(String pubdateTime) {
		this.pubdateTime = pubdateTime;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}


	public String[] getNsTag() {
		return nsTag;
	}

	public void setNsTag(String[] nsTag) {
		this.nsTag = nsTag;
	}

	public String[] getNtTag() {
		return ntTag;
	}

	public void setNtTag(String[] ntTag) {
		this.ntTag = ntTag;
	}

	public String[] getNrTag() {
		return nrTag;
	}

	public void setNrTag(String[] nrTag) {
		this.nrTag = nrTag;
	}

	public String[] getNpTag() {
		return npTag;
	}

	public void setNpTag(String[] npTag) {
		this.npTag = npTag;
	}

	public String[] getTag() {
		return tag;
	}

	public void setTag(String[] tag) {
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

	public String getNp() {
		return np;
	}

	public void setNp(String np) {
		this.np = np;
	}

	@Override
	public void exchange(Item obj) {



	}

	@Override
	public VoExchange to(Item obj) {

		ItemVo vo=new ItemVo();
		if(obj.getKeyword()!=null) {
			String[] r = (String[]) obj.getKeyword().entrySet().stream().map(x -> x.getKey() + "|" + x.getValue()).toArray();
			vo.setTag(r);
		}

		vo.setCatagory(obj.getCatagory());
		vo.setCaId(obj.getCatagoryId());
		vo.setTitle(obj.getTitle());
		vo.setScore(obj.getScore());
		vo.setManualScore(obj.getManualScore());
		vo.setAuthor(obj.getMeta().get("author"));
		vo.setRedirectUrl(obj.getMeta().get("ogUrl"));
		vo.setText(obj.getContent());

		if (obj.getFirstFetchTime() != null)
			vo.setCreateTime(DateUtils.format("YYYY-MM-dd HH:mm", new Date(Long.valueOf(obj.getFirstFetchTime())))
			);


		if (obj.getFirstPubTime() != null)
			vo.setPubdate(DateUtils.format("YYYY-MM-dd HH:mm", new Date(Long.valueOf(obj.getFirstPubTime()))));

//		if (obj.getContent() != null) {
//			MainWordExtractor extractor=MainWordExtractor.getInstance();
//			try {
//				Map<String,String> kws= extractor.tokenize(obj.getContent());
//				vo
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (JcsegException e) {
//				e.printStackTrace();
//			}
//
//		}
		return vo;
	}
}
