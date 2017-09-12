package com.linghua.hdds.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;

import com.google.gson.Gson;
import com.rongji.cms.webservice.client.json.CatalogClient;
import com.rongji.cms.webservice.client.json.CmsClientFactory;
import com.rongji.cms.webservice.client.json.SiteClient;
import com.rongji.cms.webservice.domain.WsCatalog;
import com.rongji.cms.webservice.domain.WsCatalogFilter;
import com.rongji.cms.webservice.domain.WsListResult;
import com.rongji.cms.webservice.domain.WsPage;
import com.rongji.cms.webservice.domain.WsSite;
import com.rongji.cms.webservice.domain.WsSiteFilter;

public class CataLogManager {
	@Autowired
	private CmsClientFactory fac;
	private static final Map<String, String> catalogs = new HashMap<>();
	private static final Map<String, Node> nodes = new HashMap<>();
	private static final Node root = new Node("", "root", "root");
	private static final Map<String, Map<String, String>> bizMapping=new HashMap<>();

	static {

		init();

//		buildTree();

	}

	public static String update(String id, String catalogs) {

		return CataLogManager.catalogs.put(catalogs, id);

	}

	public static Map<String, Node> getNodeMap() {
		return nodes;
	}

	public static List<Node> getCatalogTree() {
		return getCatalogTree(null);
	}

	public static List<Node> getCatalogTree(String siteId){
		if(siteId==null)
			return root.getChildren().get(0).getChildren();
		
		for(Node node:root.getChildren()){
			if(node.getCaId().equals(siteId))
				return node.getChildren();
		}
		
		throw new NullPointerException("没有找到该站点："+siteId);
	}
	
	private static void clear() {
		catalogs.clear();
		bizMapping.clear();
		nodes.clear();
		root.getChildren().clear();
	}

	public static synchronized void init() {
		clear();
		
		CmsClientFactory fac = new CmsClientFactory("http://cms.work.net", "00000002", "A7dCV37Ip96%86");

		SiteClient sl = fac.getSiteClient();

		WsPage page = new WsPage();
		page.setPageSize(1000);
		WsSiteFilter siteFilter = new WsSiteFilter();

		try {
			WsListResult<WsSite> sites = sl.findSite(siteFilter, page);
			sites.getList().forEach(x -> {
				String siteId = x.getSiteId();

				CatalogClient catalogClient = fac.getCatalogClient();
				WsCatalogFilter filter = new WsCatalogFilter();
				filter.setSiteId(siteId);
				List<String> list = new ArrayList<>();
				list.add("1");
				filter.setStatuses(list);
				// filter.setCaName(catagory.trim());
				// filter.setSiteId("190014");
				// filter.setSiteId("190019");
				Node topNode=new Node("", siteId, x.getSiteName());
				Map<String, String> catalogs=new HashMap<>();
				synchronized (nodes) {
					WsListResult<WsCatalog> caNa;
					Map<String, Node> tmpNodes=new HashMap<>();
					try {
						
						caNa = catalogClient.findCatalog(filter, page);
						for (WsCatalog cata : caNa.getList()) {
							if (cata.getMetaKeywords() != null) {
								for (String name : cata.getMetaKeywords().split(",|，")) {
									catalogs.put(name, cata.getCaId());
								}
							}							
							catalogs.put(cata.getCaName(), cata.getCaId());
							nodes.put(cata.getCaId(), new Node(cata.getCaPid(), cata.getCaId(), cata.getCaName()));
							tmpNodes.put(cata.getCaId(), new Node(cata.getCaPid(), cata.getCaId(), cata.getCaName()));
						}
						if(siteId.equals("190014"))
						   bizMapping.put("headlines", catalogs);
						else bizMapping.put("govheadlines", catalogs);
						buildTree(tmpNodes,topNode);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			});

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	public static String findCaIdByName(String bizCode,String catagory) {
		
		return bizMapping.get(bizCode).get(catagory);
	}
	public static String findCaIdByName(String catagory) {

		return catalogs.get(catagory);
	}

	public static String findCaNameById(String caId) {
		return nodes.get(caId).getCaName();
	}

	public static String findPId(String caId) {
		Node node = nodes.get(caId);
		if (node.getpId() == null)
			return node.getCaId();
		return node.getpId();
	}

	private static List<Node> buildTree(Map<String, Node> tmpNodes,Node topNode) {

		for (Entry<String, Node> kv : tmpNodes.entrySet()) {
			Node v = kv.getValue();
			String pid = v.getpId();
			if (pid == null) {
				topNode.addChildren(v);
			} else {
				tmpNodes.get(pid).addChildren(v);
			}
		}
		root.addChildren(topNode);
		return root.getChildren();
	}

	public static String getCaName(String caId) {
		return nodes.get(caId).getCaName();
	}

	public static void main(String[] args) {
		
		CataLogManager.init();
		System.out.println(CataLogManager.getCatalogTree().size());
		System.out.println(new Gson().toJson(CataLogManager.getCatalogTree()));
		
	}

}

class Node {

	private String pId;
	private String caId;
	private String caName;
	private List<Node> children = new ArrayList<>();

	public Node(String pId, String caId, String caName) {
		super();
		this.pId = pId;
		this.caId = caId;
		this.caName = caName;

	}

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public String getCaId() {
		return caId;
	}

	public void setCaId(String caId) {
		this.caId = caId;
	}

	public String getCaName() {
		return caName;
	}

	public void setCaName(String caName) {
		this.caName = caName;
	}

	public List<Node> getChildren() {
		return children;
	}

	public void setChildren(List<Node> children) {
		this.children = children;
	}

	public void addChildren(Node child) {
		if (children == null)
			children = new ArrayList<>();
		children.add(child);
	}

	public boolean hasChldren() {
		return children != null && children.size() > 0;
	}

	public String toString() {

		StringBuffer buff = new StringBuffer();
		buff.append("[  ").append(this.caName);

		if (hasChldren()) {

			for (Node node : getChildren()) {
				buff.append("\n").append("\t").append(">>>>");
				buff.append(node.toString());
			}
			buff.append(" ]\n  ");
		}
		buff.append(" ]\n  ");

		return buff.toString();
		// return this.pId+"::"+this.caName+":"+
		// (hasChldren()?getChildren().size():0);
	}

}
