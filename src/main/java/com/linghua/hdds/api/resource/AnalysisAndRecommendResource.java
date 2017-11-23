package com.linghua.hdds.api.resource;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.QueryParam;

import com.linghua.hdds.common.*;
import com.linghua.hdds.preference.model.BaseTagWithLabelRecommendModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.linghua.hdds.api.service.ItemService;
import com.linghua.hdds.api.service.UserService;
import com.linghua.hdds.meta.TwoTuple;
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

	private static final Set<Item.FIELDS> FIELDS=new HashSet<>();

    {
        FIELDS.add(Item.FIELDS.CATAGORY);
        FIELDS.add(Item.FIELDS.META);
        FIELDS.add(Item.FIELDS.TITLE);
        FIELDS.add(Item.FIELDS.HIS);
        FIELDS.add(Item.FIELDS.FIRSTPUBTIME);
    }
    @RequestMapping("/rmdByT/{biz}/{filter}/{hm}")
	public List<ArticleVo> recommendByTag(@PathVariable("biz") String biz,@PathVariable("filter") String filter,@PathVariable("hm") int hm,@RequestBody Map<String,Float> tags){
        if(tags ==null ||tags.size() ==0) {
	        return new ArrayList<>();
        }
        if(hm >10){
            hm = 10;
        }
        BaseTagWithLabelRecommendModel model = BaseTagWithLabelRecommendModel.getInstance(TableUtil.getEndKey(1, Calendar.WEEK_OF_YEAR));
        try {
            return getArticleFromHbaseByIds(biz,model.recommend(tags,null),hm,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	@RequestMapping("/rmdByUser/{biz}/{ssCode}/{uid}/{dir}")
	@ResponseBody
	public List<ArticleVo> recommendByUser(@PathVariable("biz") String biz,@PathVariable("ssCode") String ssCode,@PathVariable("uid") String uid,@PathVariable("dir") String dir){


		int hm = 20;
        System.out.println(biz+"----"+uid+"----"+dir);
        String row =TableUtil.idReverseAndBuild(uid);
        final User user = StringUtils.hasLength(row) ? userService.get(biz,row) : null;
		if(user==null)
		    return null;

		System.out.println(user.getGraph()+".....................");
		BaseTagWithLabelRecommendModel model=BaseTagWithLabelRecommendModel.getInstance(TableUtil.getEndKey(1, Calendar.MONTH));
		List<TwoTuple<String, Float>> result= model.recommend(user);

		try {
			return getArticleFromHbaseByIds(biz,result,hm,true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(".............."+result.size());

		return null;
		// 获取基于用户历史画像的文章和用户订阅的文章和系统管理员推荐的一些文章加权
//		return "callback("+new Gson().toJson(result.subList(0, hm))+")";
	}
    private List<ArticleVo> getArticleFromHbaseByIds(String biz,List<TwoTuple<String, Float>> result,int hm,boolean random) throws Exception {

        long begin = System.currentTimeMillis();
        int length=result.size();

        if(length ==0)
            return null;
        System.out.println("adaptable article found:"+length);
        if(length <=hm) {
            hm = length;
        }
        List<String> ids =new ArrayList<>(hm);
        if(random){
            Random r=new Random();
            for(int i=0;i<hm;i++){
                int index=r.nextInt(length);
                if(result.get(index)!=null)
                    ids.add(result.get(index)._1);
            }
        }else {
            for(int i=0;i<hm;i++){
                ids = result.stream().map(x->x._1).collect(Collectors.toList());
            }
        }

        List<Item> items = itemService.get(biz,ids,FIELDS);
        return items.stream().map(item -> {
            if(item.getId() ==null){
                System.out.println("item is null:"+item);
                return null;
            }
            ArticleVo vo =new ArticleVo();

            vo.put("id",TableUtil.IdReverse(item.getId()));
            vo.put("title",item.getTitle());
            vo.put("caName",item.getCatagory());
            try {
                if (item.getFirstPubTime() != null) {
                    vo.put("pubDate",DateUtils.format("YYYY-MM-dd HH:mm:ss", new Date(Long.valueOf(item.getFirstPubTime()))));
                }
            }catch (Exception e){
                vo.put("pubDate",DateUtils.format("YYYY-MM-dd HH:mm:ss", new Date()));

            }
            if(item.getMeta()!=null) {
                vo.put("source", item.getMeta().get("ogSite"));
                vo.put("img",item.getMeta().get("img"));
            }
            return vo;
        }).collect(Collectors.toList());

    }
	private List<ArticleVo> getArticleByIds(List<TwoTuple<String, Double>> result,int hm,boolean random) throws Exception {

        long begin = System.currentTimeMillis();
        int length=result.size();
        String[] ids = null;
        System.out.println("adaptable article found:"+length);
        if(length <=hm) {
            hm = length;
        }
        ids=new String[hm];
        if(random){
            Random r=new Random();
            for(int i=0;i<hm;i++){
                int index=r.nextInt(length);
                if(result.get(index)!=null)
                  ids[i]=TableUtil.IdReverse(result.get(index)._1);
            }
        }else {
            for(int i=0;i<hm;i++){
                ids[i]=TableUtil.IdReverse(result.get(i)._1);
            }
        }
        try {
            WsArticleFilter filter=new WsArticleFilter();
            filter.setContainsContent(false);
            filter.setArIds(ids);
            WsPage page=new WsPage();
            page.setPageSize(hm);
            List<ArticleVo> articleVoList=fac.getArticleClient().findArticleVos(filter, page).getList();

            for(ArticleVo ar: articleVoList){
            	if(ar.get("pubDate")!=null)
            	  ar.put("pubDate", DateUtils.format(null,new Date(Long.valueOf(ar.get("pubDate")))));
			}
            return articleVoList;
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            System.out.println("cost time fetch artcle from cms:"+(System.currentTimeMillis() - begin));
        }
        return null;
    }

    @RequestMapping("/byCatagory/{biz}/{ssCode}/{ca}/{start}/{hm}")
    @ResponseBody
    public List<ArticleVo> sortByCatagory(@PathVariable("biz") String biz,@PathVariable("ssCode") String ssCode,@PathVariable("ca") String ca,@PathVariable("start") int start,@PathVariable("hm") int hm) {
        BaseTagWithLabelRecommendModel model = BaseTagWithLabelRecommendModel.getInstance(TableUtil.getEndKey(1, Calendar.MONTH));

//        try {
//            return getArticleByIds(model.getByCatalog(ca, start, hm), hm,false);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        return null;
	}
    @RequestMapping("/ctxsim/{biz}/{iid}")
	public List<ArticleVo> contextSimilar(@PathVariable String biz,@PathVariable String iid){
		SimHash sim=new SimHash("");
        BigInteger big;
		return null;
	}

	@RequestMapping("/recommendFromCatalog/{biz}/{caId}/{page}")
    public List recommendFromCatalog(@PathVariable("biz")String biz,@PathVariable("caId")String caId,@PathVariable("page")int page,@RequestBody Map<String,Integer> behavior ){

        Assert.hasLength(biz,"");
        Assert.hasLength(caId,"");
        String caName = CataLogManager.getCaName(caId);

        if(behavior ==null ||behavior.size() ==0){

        }

	    return null;
    }

	@RequestMapping(value = "/classify")
	@ResponseBody
	public String classify(@QueryParam("biz_code") String biz_code,@QueryParam("ss_code")String ss_code,@RequestBody String text)
			throws UnsupportedEncodingException {

		
		String model = "NaiveBayes";
		
		String uri = "http://slave2:9999/mining/classify?biz_code="
				+ biz_code + "&ss_code=" + ss_code + "&model=" + model;
		String result=HttpClientResource.post(text, uri);
		if(result ==null)
			return "{}";

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