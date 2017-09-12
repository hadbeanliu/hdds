package com.linghua.hdds.api.resource;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.linghua.hdds.api.service.ItemService;
import com.linghua.hdds.api.service.UserService;
import com.linghua.hdds.common.CataLogManager;
import com.linghua.hdds.common.HttpClientResource;
import com.linghua.hdds.common.TableUtil;
import com.linghua.hdds.meta.TwoTuple;
import com.linghua.hdds.preference.model.BaseTagRecommendModel;
import com.linghua.hdds.store.Item;
import com.linghua.hdds.store.User;
import com.rongji.cms.webservice.client.json.CmsClientFactory;
import com.rongji.cms.webservice.domain.WsArticleFilter;
import com.rongji.cms.webservice.domain.WsArticleSynData.ArticleVo;
import com.rongji.cms.webservice.domain.WsPage;

@RestController
@RequestMapping("/taste")
public class AnalysisAndRecommendResource {
	
	@Autowired
	private UserService userService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private CmsClientFactory fac;
	
	private static Gson gson=new Gson();

	
	@RequestMapping("/rmmd/{biz}/{ssCode}/{uid}")
	@ResponseBody
	public List<ArticleVo> recommendByMemory(@PathVariable("biz") String biz,@PathVariable("ssCode") String ssCode,@PathVariable("uid") String uid){

		
		int hm = 6;

		final User user = StringUtils.hasLength(uid) ? userService.get(biz,uid) : null;
		System.out.println(user.getGraph()+".....................");
		BaseTagRecommendModel model=BaseTagRecommendModel.getInstance(TableUtil.getEndKey(1, Calendar.WEEK_OF_YEAR));
		List<TwoTuple<String, Double>> result= model.recommend(user);
		int length=result.size();
		if(length<6){
			hm=length;
			WsArticleFilter filter=new WsArticleFilter();
			filter.setArIds((String[]) result.stream().map(x->TableUtil.IdReverse(x.first)).toArray());
			WsPage page=new WsPage();
			try {
				return fac.getArticleClient().findArticleVos(filter, page).getList();
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}
		String[] ids=new String[hm];
		for(int i=0;i<hm;i++){
			Random r=new Random();
			int index=r.nextInt(length);
			ids[i]=TableUtil.IdReverse(result.get(index).first);
		}
		try {
			WsArticleFilter filter=new WsArticleFilter();
			filter.setArIds(ids);
			WsPage page=new WsPage();
			return fac.getArticleClient().findArticleVos(filter, page).getList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(".............."+result.size());

		return null;
		// 获取基于用户历史画像的文章和用户订阅的文章和系统管理员推荐的一些文章加权
//		return "callback("+new Gson().toJson(result.subList(0, hm))+")";
	}
	

	@RequestMapping(value = "/classify")
	@ResponseBody
	public String classify(@QueryParam("biz_code") String biz_code,@QueryParam("ss_code")String ss_code,@RequestBody String text)
			throws UnsupportedEncodingException {

		
		String model = "NaiveBayes";
		
		String uri = "http://slave2:9999/mining/classify?biz_code="
				+ biz_code + "&ss_code=" + ss_code + "&model=" + model;
		String result=HttpClientResource.post(text, uri);

		StringTokenizer strToken=new StringTokenizer(result,"()");
		List<String> resultList=new ArrayList<>();
		List<Meta> metas=new ArrayList<>();
		while(strToken.hasMoreTokens()){
			String tok=strToken.nextToken();
			if(tok.equals(","))
				continue;
			String[] split=tok.split(",");
			String caId= CataLogManager.findCaIdByName(biz_code,split[0]);
			float value=0.0f;
			if(split[1].length()<5)
				value=Float.valueOf(split[1]);
				else value =Float.valueOf(split[1].substring(0, 5));
			metas.add(new Meta(caId,split[0],value));
			
			resultList.add(tok);
	}
		
		return gson.toJson(metas);
	}

	@RequestMapping(value = "/log")
	@ResponseBody
	public String buildUserGraph(HttpServletRequest req) {

		String act = req.getParameter("act");
		String iid = req.getParameter("iid");
		int time = Integer.valueOf(req.getParameter("time"));

		if (time < 10) {
			return null;
		}

		HttpSession session = req.getSession();

		Map<String, Float> graph = null;
		if (session.getAttribute("graph") == null) {
			graph = new HashMap<String, Float>();
		} else
			graph = (Map<String, Float>) session.getAttribute("graph");

		int logN = session.getAttribute("logN") == null ? 0 : (int) session
				.getAttribute("logN");
		Item i = itemService.get("headlines", TableUtil.IdReverse(iid),
				Item.FIELDS.KEYWORD);

		if (i.getKeyword() == null)
			return null;
		for (Entry<String, Float> kv : i.getKeyword().entrySet()) {
			if (graph.containsKey(kv.getKey())) {
				graph.put(kv.getKey(),
						(graph.get(kv.getKey()) * logN + kv.getValue())
								/ (logN + 1));
			} else
				graph.put(kv.getKey(), kv.getValue() / (logN + 1));
		}
		System.out.println(new Gson().toJson(session.getAttribute("graph")));

		session.setAttribute("graph", graph);
		session.setAttribute("logN", logN + 1);

		return null;

	}

}
class Meta{
	
	private String caId;
	private String caName;
	private float value;
	
	public Meta(String caId,String caName,float value){
		this.caId=caId;
		this.caName=caName;
		this.value=value;
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



	public double getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}

	@Override
	public String toString() {
		
		return this.caName+","+this.value;
	}
	
	
}