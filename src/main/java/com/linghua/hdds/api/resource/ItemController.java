package com.linghua.hdds.api.resource;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;

import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.FilterList.Operator;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.log4j.Logger;
import org.lionsoul.jcseg.tokenizer.core.ILexicon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.linghua.hdds.api.response.ItemVo;
import com.linghua.hdds.api.service.ItemService;
import com.linghua.hdds.common.CataLogManager;
import com.linghua.hdds.common.DateUtils;
import com.linghua.hdds.common.HtmlParser;
import com.linghua.hdds.common.HttpClientResource;
import com.linghua.hdds.common.MainWordExtractor;
import com.linghua.hdds.common.NumberFormat;
import com.linghua.hdds.common.TableUtil;
import com.linghua.hdds.meta.TwoTuple;
import com.linghua.hdds.store.Item;
import com.rongji.cms.webservice.client.json.ArticleClient;
import com.rongji.cms.webservice.client.json.CmsClientFactory;
import com.rongji.cms.webservice.domain.WsArticleFilter;
import com.rongji.cms.webservice.domain.WsArticleSynData.ArticleVo;
import com.rongji.cms.webservice.domain.WsCallResult;
import com.rongji.cms.webservice.domain.WsListResult;
import com.rongji.cms.webservice.domain.WsPage;

@RestController
@RequestMapping("/item")
public class ItemController {

	private static Logger LOG = Logger.getLogger(ItemController.class);

	@Autowired
	private ItemService itemService;

	@Autowired
	CmsClientFactory cmsFactory;

	private static Set<Item.FIELDS> FIELDS = new HashSet<>();
	private static Gson gson = new Gson();

	static {
		FIELDS.add(Item.FIELDS.KEYWORD);
		FIELDS.add(Item.FIELDS.STICKTOP);
		FIELDS.add(Item.FIELDS.MANUALSCORE);
		FIELDS.add(Item.FIELDS.META);
		FIELDS.add(Item.FIELDS.CATAGORY);
		FIELDS.add(Item.FIELDS.TITLE);

	}
		
	@RequestMapping("/list/{caId}/{bizCode}")
	public List<Item> listFromCaid(@PathVariable("caId")String caId,@PathVariable("bizCode")String bizCode){
		
		Assert.hasLength(caId, "栏目ID不能为空");
		Assert.hasLength(bizCode, "业务代码不能为空");
		
		int hm =15;

		return null;
	}
	
	@RequestMapping("/getDetail")
	@ResponseBody
	public String getDetail(@QueryParam(value = "biz_code") String biz_code, @QueryParam(value = "iid") String iid,
			@QueryParam(value = "split") boolean split) {

		Assert.notNull(iid, "item id must be required!");
		Assert.notNull(biz_code, "biz_code must be required!");

		ArticleClient client = cmsFactory.getArticleClient();

		WsArticleFilter filter = new WsArticleFilter();

		filter.setArIds(iid);
		WsPage page = new WsPage();

		try {
			ArticleVo article = client.findArticleVos(filter, page).getList().get(0);

			String content = article.get("content");
			if (content == null || !split)
				return gson.toJson(article);
			content = content.replaceAll("\\.", "9879");
			List<TwoTuple<String, String>> words = null;
			try {
				MainWordExtractor extractor=MainWordExtractor.getInstance();
				words = extractor.simpleTokenizeWithPart(HtmlParser.delHTMLTag(content));
				List<String> keyword = new ArrayList<>();
				for (TwoTuple<String, String> w : words) {
					if (w.second == null)
						continue;
					String t = w.second;
					if (t.startsWith("n") || t.equals("en") && w.first.length() > 1)
						keyword.add(w.first);
				}
				String recString = HttpClientResource.post(gson.toJson(keyword),
						"http://slave2:9999/mining/extractkw?biz_code=headlines" + "&ss_code=user-analys");
				// List<String>
				// words=extractor.tokenizeWithoutPart(item.getContent());
				Map<String, String> recTags = gson.fromJson(recString, new TypeToken<Map<String, String>>() {
				}.getType());

				for (TwoTuple<String, String> word : words) {
					String t = word.second;
					StringBuffer sb = new StringBuffer();
					sb.append("<em class=\"margin-l");
					if (recTags.containsKey(word.first)) {
						sb.append(" tagFlag");
					}

					if (t == null)
						sb.append("\">").append(word.first).append("</em>");
					else if (t.startsWith("ns"))
						sb.append(" ns\">").append(word.first).append("</em>");
					else if (t.startsWith("nr"))
						sb.append(" nr\">").append(word.first).append("</em>");
					else if (t.startsWith("nt")) {
						sb.append(" nt\">").append(word.first).append("</em>");
					} else if (t.startsWith("np")) {
						sb.append(" np\">").append(word.first).append("</em>");
					} else
						sb.append("\">").append(word.first).append("</em>");
					word.second = sb.toString();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			char[] originChars = content.toCharArray();
			String lowCaseContent = content.toLowerCase();
			char[] chars = lowCaseContent.toCharArray();
			int index = 0;
			StringBuffer sb = new StringBuffer();
			for (TwoTuple<String, String> word : words) {
				String w = word.first;
				String tmp = lowCaseContent.substring(index);
				if (tmp.indexOf(w) == -1)
					continue;
				char[] ch = w.toCharArray();
				boolean flag = true;
				if (index >= chars.length) {
					flag = false;
				}
				while (flag) {

					if (ch[0] == chars[index]) {
						flag = false;
						for (int j = 1; j < ch.length; j++) {
							if (ch[j] != chars[index + j]) {
								flag = true;
								break;
							}
						}
						if (flag) {
							sb.append(originChars[index]);
							index++;
						} else {
							sb.append(word.second);
							index += ch.length;
						}
					} else {

						if (chars[index] == '<' && ((index + 4) < chars.length) && chars[index + 1] == 'i'
								&& chars[index + 2] == 'm' && chars[index + 3] == 'g') {
							sb.append("<img");
							index += 3;

							do {
								index++;
								sb.append(originChars[index]);
							} while (chars[index] != '>');
							index++;
						} else if (chars[index] == '<' && ((index + 4) < chars.length) && chars[index + 1] == 'a') {
							sb.append("<a");
							index += 1;

							do {
								index++;
								sb.append(originChars[index]);
							} while (chars[index] != '>');
							index++;
						} else {

							sb.append(originChars[index]);
							index++;
						}
					}
					if (index >= chars.length) {
						flag = false;
					}
				}

			}
			if (index < chars.length)
				sb.append(content.substring(index));

			article.put("content", sb.toString().replaceAll("9879", "."));

			return gson.toJson(article);

			// Map<String, String> ar=new HashMap<>();
			// ar.put("title", item.getTitle());
			// ar.put("author", item.getMeta().get("author"));
			// ar.put("source", item.getMeta().get("ogSite"));
			// ar.put("redirectUrl", item.getMeta().get("ogUrl"));
			// ar.put("createTime_title", value)
			// ar.put("score", String.valueOf(item.getScore()));
			// ar.put("tag", gson.toJson(item.getKeyword()));
			// ar.put("manualScore", String.valueOf(item.getManualScore()));
			// ar.put("text",item.getContent());

			// ar.put(key, value)
		} catch (Exception e) {

			e.printStackTrace();
		}
		return "{}";

	}

	@RequestMapping("/speechpart")
	@ResponseBody
	public String speechPart(@QueryParam(value = "word") String word, @QueryParam(value = "speech") String speech,
			@QueryParam(value = "type") String type) {

		Assert.hasText(word, "word cant be null");

		int tp = ILexicon.CJK_WORD;
		try {
			word = new String(word.getBytes("ISO8859-1"), "UTF-8");

			if (type == null)
				tp = ILexicon.CJK_WORD;
			else if (type.equals("del"))
				tp = -1;
			else if (type.equals("stop"))
				tp = ILexicon.STOP_WORD;
			else if (type.equals("unit"))
				tp = ILexicon.CJK_UNIT;
			else if (type.equals("single"))
				tp = ILexicon.CJK_CHAR;
			MainWordExtractor extractor=MainWordExtractor.getInstance();
			word = word.trim().replace("\n", "").toLowerCase();
			extractor.addIWord(speech, tp, word, true);

			extractor.exportToLocal(speech, tp, word);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "fail";
		}

		return "success";
	}

	@RequestMapping("/get")
	@ResponseBody
	public String get(@QueryParam(value = "biz_code") String biz_code, @QueryParam(value = "iid") String iid) {

		Assert.notNull(iid, "item id must be required!");
		Assert.notNull(biz_code, "biz_code must be required!");
		Item item = itemService.get(biz_code, TableUtil.IdReverse(iid));
		if(item==null)
			return "{}";
		try {

			Map<String, String> ar = new HashMap<>();
			ar.put("title", item.getTitle());
			ar.put("author", item.getMeta().get("author"));
			ar.put("source", item.getMeta().get("ogSite"));
			ar.put("redirectUrl", item.getMeta().get("ogUrl"));
			ar.put("score", String.valueOf(item.getScore()));
			ar.put("tag", gson.toJson(item.getKeyword()));
			ar.put("manualScore", String.valueOf(item.getManualScore()));
			ar.put("text", item.getContent());

			if (item.getFirstFetchTime() != null)
				ar.put("createTime_title",
						DateUtils.format("YYYY-MM-dd HH:mm", new Date(Long.valueOf(item.getFirstFetchTime()))));

			try {
				if (item.getFirstFetchTime() != null)
					ar.put("createTime_title",
							DateUtils.format("YYYY-MM-dd HH:mm", new Date(Long.valueOf(item.getFirstFetchTime()))));

				if (item.getFirstPubTime() != null)
					ar.put("pubDate_title",
							DateUtils.format("YYYY-MM-dd HH:mm", new Date(Long.valueOf(item.getFirstPubTime()))));
				MainWordExtractor extractor=MainWordExtractor.getInstance();

				if (item.getContent() != null)
					ar.putAll(extractor.tokenize(item.getContent()));
				List<String> words = extractor.tokenizeWithoutPart(item.getContent());
				String recString = HttpClientResource.post(gson.toJson(words),
						"http://slave2:9999/mining/extractkw?biz_code=headlines" + "&ss_code=user-analys");
			
				Map<String, String> recTags = gson.fromJson(recString, new TypeToken<Map<String, String>>() {
				}.getType());
				ar.putAll(recTags);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return gson.toJson(ar);

		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;


	}

	@RequestMapping("/find")
	@ResponseBody
	public String find(@QueryParam(value = "biz_code") String biz_code, @QueryParam(value = "ca") String ca,
			@QueryParam(value = "hm") int hm, @QueryParam(value = "lr") String lr) {

		Assert.notNull(biz_code, "biz_code must be required!");

		FilterList filters = new FilterList(Operator.MUST_PASS_ALL);
		filters.addFilter(new SingleColumnValueFilter("f".getBytes(), "lb".getBytes(), CompareOp.EQUAL,
				new BinaryComparator(ca.getBytes())));
		filters.addFilter(new PageFilter(hm));
		List<Item> item = itemService.find(biz_code, null, filters, hm);

		return "callback(" + new Gson().toJson(item) + ")";

	}

	private Map<String, String> toMap(Map<String, String[]> map) {
		Map<String, String> args = new HashMap<String, String>();
		if (map == null || map.size() == 0)
			return args;
		for (Entry<String, String[]> kv : map.entrySet()) {
			if (!"".equals(kv.getValue()[0])&&!kv.getValue()[0].startsWith("[db"))
				args.put(kv.getKey(), kv.getValue()[0]);
		}
		return args;
	}

	@RequestMapping("/save")
	@ResponseBody
	public String save(HttpServletRequest req) {

		try {
			Gson gson = new Gson();
			String page = req.getParameter("page");
			Item item = gson.fromJson(URLDecoder.decode(page, "utf-8"), Item.class);
			Map<CharSequence, String> pageMap = gson.fromJson(URLDecoder.decode(page, "utf-8"),
					new TypeToken<Map<String, Object>>() {
					}.getType());

			String bizCode = req.getParameter("biz_code");
			String key = pageMap.get("id");
			String bid = (String) pageMap.get("batchId");
			//
			Assert.notNull(bizCode, "biz_code must required specified");
			Assert.notNull(key, "key must required specified");

			item.setFirstFetchTime(pageMap.get("fetchTime"));

			if (!StringUtils.isEmpty(pageMap.get("tag"))) {
				Map<String, Float> keywords = Maps.newHashMap();
				String[] tags = ((String) pageMap.get("tag")).split(",| ");
				float value = Float.valueOf(NumberFormat.decimalFormat(1.0f / tags.length));
				for (String t : tags) {
					if (!StringUtils.isEmpty(t))
						keywords.put(t.trim(), value);
				}
				item.setKeyword(keywords);
			}

			itemService.put(bizCode, TableUtil.IdReverse(bid), item);

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
			return null;
		}

		return "success";

	}

	@RequestMapping("/update")
	@ResponseBody
	@POST
	public String upDateItem(@RequestBody ItemVo vo) {

//		ItemVo vo = gson.fromJson(itemVo, ItemVo.class);
		String iid = vo.getId();
		Assert.isTrue(iid != null);
		Assert.isTrue(!vo.getCaId().equals("null"));
		Item item = new Item();
		item.setCatagory(vo.getCatagory());

		ArticleClient client = cmsFactory.getArticleClient();

		WsArticleFilter filter = new WsArticleFilter();

		filter.setArIds(iid);
		WsPage page = new WsPage();

		try {

			ArticleVo article = client.findArticleVos(filter, page).getList().get(0);
			article.put("caId", vo.getCaId());
			if (vo.getTitle() != null) {
				article.put("title", vo.getTitle());
				item.setTitle(vo.getTitle());
			}
			if (!StringUtils.isEmpty(vo.getTag())) {
				StringBuffer arTags = new StringBuffer();
				Map<String, Float> keyword = new HashMap<>();
				float count = 0;
				for (String t : vo.getTag()) {
					String[] tagScore = t.split("\\|");

					if (tagScore.length == 2) {
						float v = Float.valueOf(tagScore[1]);
						keyword.put(tagScore[0], v);
						count += v;
					} else {
						float v = Float.valueOf(NumberFormat.decimalFormat(new Random().nextFloat()));
						keyword.put(tagScore[0], v);
						count += v;
					}
					arTags.append(tagScore[0]).append(",");
				}
				for (String tag : keyword.keySet()) {
					keyword.put(tag, Float.valueOf(NumberFormat.decimalFormat(keyword.get(tag) / count)));
				}
				item.setKeyword(keyword);
				article.put("tags", arTags.toString().replaceAll(" ", "##"));
			}
			if(vo.getNt()!=null){
				article.put("nt", vo.getNt());
				String[] nt=vo.getNt().split(",");
				for(String kw:nt)
					item.getKeyword().put(kw, 1.0f);
				
			}
			if(vo.getNr()!=null){
				article.put("nr", vo.getNr());
				String[] nr=vo.getNr().split(",");
				for(String kw:nr)
					item.getKeyword().put(kw, 1.0f);
			}
			if(vo.getNs()!=null){
				article.put("ns", vo.getNs());
				String[] ns=vo.getNs().split(",");
				for(String kw:ns)
					item.getKeyword().put(kw, 1.0f);
			}
			item.setTitle(vo.getCaId());
			item.setManualScore(Float.valueOf(vo.getManualScore()));
			article.put("status", "1");

			WsCallResult status = client.saveArticleSynData(vo.getCaId(), article);

			if (status.getRet() == 0) {
				if (item.getKeyword().size() > 0) {
					itemService.delete(vo.getBizCode(), TableUtil.IdReverse(iid),Item.HBASE_MAPPING.get(Item.FIELDS.KEYWORD.getIndex()));
				}
				
				itemService.put(vo.getBizCode(), TableUtil.IdReverse(status.getRetValue()), item);
				LOG.info("update: ["+iid+"] success");
				return "success";
			}
			LOG.info("update: ["+iid+"] fail ["+status.getMsg()+"]");

			return " update fail:" + status.getMsg();
		} catch (Exception e) {
			
			StringBuffer sb=new StringBuffer();
			for(StackTraceElement ste:e.getStackTrace())
				sb.append(ste.toString()).append("\n");
			
			LOG.error("update: ["+iid+"] fail ["+sb.toString()+"]");

			e.printStackTrace();
		}
		
		return " update fail:";

	}

	@RequestMapping("/reload")
	@ResponseBody
	public String reload() {
		try {
			CataLogManager.init();
			
		} catch (Exception e) {
			e.printStackTrace();
			return "cms缓存清除失败";
		}
		String result = HttpClientResource.doSend("http://cms.work.net/wNewsRecommend.sp?act=clearCache", null, "get");
		if (result.contains("成功"))
			return "success";
		else
			return "搜索引擎缓存清除失败";

	}

	@RequestMapping("/updateAll")
	@POST
	@ResponseBody
	public String updateAll(@RequestBody String[] ids, @QueryParam("biz_code") String biz_code,
			@QueryParam("caId") String caId) {



		// this.itemService.delete(bizCode, row, family, qualifier);
		Assert.hasLength(caId);
		String caName = CataLogManager.getCaName(caId);
		ArticleClient arClient = cmsFactory.getArticleClient();
		List<String> success = new ArrayList<>();
		List<String> fail = new ArrayList<>();
		WsArticleFilter filter = new WsArticleFilter();

		filter.setArIds(ids);
		WsPage page = new WsPage();
		page.setPageSize(5000);
		try {
			WsListResult<ArticleVo> ars = arClient.findArticleVos(filter, page);
			for (ArticleVo vo : ars.getList()) {

				vo.put("caId", caId);
				vo.put("status", "1");
				WsCallResult status = arClient.saveArticleSynData(caId, vo);
				if (status.getRet() == 0) {
					success.add(TableUtil.IdReverse(vo.get("id")));
				} else {

					fail.add(vo.get("title") + ":" + status.getMsg());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		String[] hbaseId = new String[success.size()];
		success.toArray(hbaseId);

		this.itemService.put(biz_code, hbaseId, Item.FIELDS.CATAGORY, caName);

		return "success number:[" + success.size() + "]+   fail number:[" + fail.size() + ":[ : +" + gson.toJson(fail)
				+ " :] ]";
		// this.itemService.delete(biz_code, id, null, null);

	}

	public String toTrain(HttpServletRequest req, @QueryParam("biz_code") String biz_code) {

		Assert.notNull(biz_code, "biz_code must be specified");

		String itemJson = req.getParameter("item");
		Item item = new Gson().fromJson(itemJson, Item.class);

		Random r = new Random();
		String key = r.nextInt(10) + "-" + System.nanoTime();
		this.itemService.put(biz_code, key, item);

		return null;
	}

	@RequestMapping("/catalogTree/{site}")
	@GET
	@ResponseBody
	public Object getCataLogTree(@PathVariable("site") String site) {

		Map<String, Object> resultMap = new HashMap<>();

		resultMap.put("catalogTree", CataLogManager.getCatalogTree(site));
		resultMap.put("mapCatalogs", CataLogManager.getNodeMap());
		return gson.toJson(resultMap);
	}

	@RequestMapping("/delete")
	@POST
	@ResponseBody
	public String delete(@RequestBody String ids, @QueryParam("biz_code") String biz_code) {

		String[] idArr = gson.fromJson(ids, new TypeToken<String[]>() {
		}.getType());

		// this.itemService.delete(bizCode, row, family, qualifier);
		String[] status = cmsFactory.getArticleClient().deleteArticles(idArr);
		if (status[0].equals("0")) {

			String[] hbaseIds = new String[idArr.length];
			for (int i = 0; i < idArr.length; i++) {
				hbaseIds[i] = TableUtil.IdReverse(idArr[i]);

			}
			itemService.delete(biz_code, hbaseIds);

			return "delete success";
		}
		return " delete fail:" + status[1];
		// this.itemService.delete(biz_code, id, null, null);

	}

	@RequestMapping("/put/random")
	@ResponseBody
	public String put(HttpServletRequest req) {
		return null;
	}

	@RequestMapping("/saves")
	@ResponseBody
	public String saveList(HttpServletRequest req) {

		return null;

	}

	@RequestMapping("/saveCrawler")
	@ResponseBody
	public String saveFromCrawler(HttpServletRequest request) {

		long begin = System.currentTimeMillis();
		// 获取表单
		Map<String, String> pageMap = toMap(request.getParameterMap());

		String biz_code = pageMap.get("biz_code");
		if (StringUtils.isEmpty(biz_code)) {
			LOG.warn("文章biz_code为空:[title:" + pageMap.get("title") + "   url:" + pageMap.get("baseUrl") + "::<"
					+ pageMap.get("content").length() + ">]");
			return "fail";
		}
		
		if(StringUtils.isEmpty(pageMap.get("html"))||"此页面是否是列表页或首页？未找到合适正文内容。".equals(pageMap.get("content"))){
			int length=pageMap.get("content")==null?0:pageMap.get("content").length();
			LOG.error("文章内容为空:[title:" + pageMap.get("title") + "   url:" + pageMap.get("baseUrl") + "::<"
					+ length + ">]");
			return "fail";
		}
		
//		if (StringUtils.isEmpty(pageMap.get("content"))|| pageMap.get("content").equals("此页面是否是列表页或首页？未找到合适正文内容。")) {
//			int length=pageMap.get("content")==null?0:pageMap.get("content").length();
//			LOG.error("文章为空:[title:" + pageMap.get("title") + "   url:" + pageMap.get("baseUrl") + "::<"
//					+ length + ">]");
//			return "fail";
//		}
		if (pageMap.get("wellFetch") != null && !pageMap.get("wellFetch").equals("1")) {

			LOG.error("内容抽取失败：[内容抽取:" + pageMap.get("wellFetch") + "][title:" + pageMap.get("title")
					+ "   url:" + pageMap.get("baseUrl") + "]");
			return "fail";

		}

		if (pageMap.get("isDis") != null && !pageMap.get("isDis").equals("1")) {
			LOG.error("投递意愿：[投递意愿:" + pageMap.get("isDis") + "][title:" + pageMap.get("title") + "   url:"
					+ pageMap.get("baseUrl") +"]");
			return "fail";

		}
		long currTime = System.currentTimeMillis();

		Item page = new Item();

		page.setContent(HtmlParser.delHTMLTag(pageMap.get("content")));
		page.setFirstFetchTime(currTime + "");
		page.setTitle(pageMap.get("title"));
		Map<String, String> meta = new HashMap<String, String>();
		if (!StringUtils.isEmpty(pageMap.get("keywords")))
			meta.put("keyword", pageMap.get("keywords"));
		page.setMeta(meta);

		if (pageMap.get("tag") != null) {
			Map<String, Float> keywords = Maps.newHashMap();
			String[] tags = ((String) pageMap.get("tag")).split(",");
			float value = Float.valueOf(NumberFormat.decimalFormat(1.0f / tags.length));
			for (String t : tags) {
				if (!StringUtils.isEmpty(t))
					keywords.put(t.trim(), value);
			}
			page.setKeyword(keywords);

		}
		if (!StringUtils.isEmpty(pageMap.get("pubDate"))) {
			page.setFirstPubTime(DateUtils.getPubDate(pageMap.get("pubDate")));
		}
		if (!StringUtils.isEmpty(pageMap.get("tsmp"))) {
			try {
				page.setFirstPubTime(
						DateUtils.format("YYYY-MM-DD HH:mm:ss", new Date(Long.valueOf(pageMap.get("tsmp").trim()))));
			} catch (NumberFormatException e) {

				e.printStackTrace();
				LOG.warn("时间戳格式错误:[title:" + pageMap.get("title") + "   url:" + pageMap.get("baseUrl") + "::<tsmp:"
						+ pageMap.get("tsmp") + ">]");

				return "fail";

			}
		}

		if (!StringUtils.isEmpty(pageMap.get("author"))) {
			meta.put("author", pageMap.get("author"));
		}

		if (pageMap.get("biz_code") != null) {

			String catagory = null;
			String autoDis = pageMap.get("autoDis");
			if ("0".equals(autoDis) && StringUtils.isEmpty(pageMap.get("catagory"))) {
				LOG.error("内容抓取失败：[自动投递:" + pageMap.get("autoDis") + "][title:" + pageMap.get("title") + "   url:"
						+ pageMap.get("baseUrl") + "::<" + catagory + ">]");

				return "fail";
			}
			if ("1".equals(autoDis) || (StringUtils.isEmpty(pageMap.get("catagory")))) {
				try {
					String result = HttpClientResource.post(page.getContent(),
							"http://slave2:9999/mining/classify?biz_code=" + pageMap.get("biz_code")
									+ "&ss_code=user-analys&model=NaiveBayes");
					LOG.info("自动投递：【"+biz_code+",标题："+pageMap.get("title")+","+pageMap.get("baseUrl")+"】");
					StringTokenizer token = new StringTokenizer(result, "()");
					int i = 0;
					StringBuffer tag = new StringBuffer();
					while (token.hasMoreTokens()) {
						String tok = token.nextToken();
						if (tok.equals(","))
							continue;
						tag.append(tok.split(",")[0]).append(" ");
						if (++i == 1)
							break;
					}

					catagory = tag.toString();
					page.getMeta().put("auto", "1");
				} catch (Exception e) {
					LOG.error("数据存储失败:[自动分类模型:\"" + biz_code + "\"未训练！][title:" + pageMap.get("title") + "   url:"
							+ pageMap.get("baseUrl") + "::<" + catagory + ">]");
					return "fail";
				}
			} else {
				catagory = pageMap.get("catagory");
			}

			if (catagory == null || "".equals(catagory)) {
				LOG.warn("找不到该类别索引:[title:" + pageMap.get("title") + "   url:" + pageMap.get("baseUrl") + "::<"
						+ catagory + ">]");

				return "fail";
			}

			try {

				String caId = CataLogManager.findCaIdByName(biz_code, catagory.trim());

				if (caId == null) {
					System.out.println(biz_code+"...................."+catagory.trim()+catagory);
					LOG.warn("爬取数据失败，类别未定义["+biz_code+"]:[title:" + pageMap.get("title") + "   url:" + pageMap.get("baseUrl") + "::<"
							+ catagory + ">]");

					return "fail";
				}

				page.setCatagory(CataLogManager.getCaName(caId));
				page.setCatagoryId(caId);
				page.setpCatagoryId(CataLogManager.findPId(caId));

				ArticleClient arClient = cmsFactory.getArticleClient();
				ArticleVo ar = new ArticleVo();

				if (page.getFirstPubTime() == null)
					page.setFirstPubTime(page.getFirstFetchTime());
				else if (page.getFirstPubTime() != null) {
					int first = page.getFirstPubTime().indexOf(":");
					int last = page.getFirstPubTime().lastIndexOf(":");
					if (first == -1) {
						ar.put("createTime", page.getFirstPubTime() + " 00:00:00");
						page.setFirstPubTime(
								String.valueOf(DateUtils.toDate("yyyy-MM-dd", page.getFirstPubTime()).getTime()));

					} else if (first < last) {
						ar.put("createTime", page.getFirstPubTime());
						page.setFirstPubTime(String
								.valueOf(DateUtils.toDate("yyyy-MM-dd HH:mm:ss", page.getFirstPubTime()).getTime()));

					} else {
						ar.put("createTime", page.getFirstPubTime() + ":00");
						page.setFirstPubTime(
								String.valueOf(DateUtils.toDate("yyyy-MM-dd HH:mm", page.getFirstPubTime()).getTime()));

					}

				}

				ar.put("content", pageMap.get("html"));
				ar.put("metaKeywords", pageMap.get("keywords"));
				if (!StringUtils.isEmpty(pageMap.get("tag"))) {
					ar.put("tags", pageMap.get("tag").trim().replaceAll(" ", "##"));
				}
				ar.put("creatorId", "00000002");
				ar.put("title", page.getTitle());
				ar.put("caId", caId);
				ar.put("isVote", page.getMeta().get("auto") == null ? "0" : "1");
				ar.put("description", pageMap.get("description"));
				String ogUrl = StringUtils.isEmpty(pageMap.get("ogUrl")) ? pageMap.get("baseUrl")
						: pageMap.get("ogUrl");

				ar.put("redirectUrl", ogUrl);
				page.getMeta().put("ogUrl", ogUrl);
				ar.put("author", page.getMeta().get("author"));

				ar.put("status", "1");
				ar.put("source", pageMap.get("ogSite"));
				// ar.put("createTime", page.getFirstPubTime());
				if (biz_code.equals("govheadlines")) {
					ar.put("siteId", "190019");
					
					if (pageMap.get("index") != null)
						ar.put("docIndex", pageMap.get("index"));
					if (pageMap.get("arCode") != null)
						ar.put("docNum", pageMap.get("arCode"));

					if (catagory.equals("政府信息公开目录")) {
						ar.setMdCode("document");
						ar.put("docPubtime", DateUtils.format("yyyy-MM-dd HH:mm:ss",
								new Date(Long.valueOf(page.getFirstPubTime()))));
					}
				}
				long time1 = System.currentTimeMillis() - begin;
				begin = System.currentTimeMillis();
				if (pageMap.get("ogSite") != null)
					page.getMeta().put("ogSite", pageMap.get("ogSite"));
				String[] rsIds = cmsFactory.getResourceClient().importOutSiteFileByUrl("190014", pageMap.get("img"));
				long time2 = System.currentTimeMillis() - begin;
				begin = System.currentTimeMillis();
				WsCallResult status = arClient.saveArticleSynData(caId, ar);
				long time3 = System.currentTimeMillis() - begin;
				begin = System.currentTimeMillis();
				if (!StringUtils.isEmpty(pageMap.get("img")))
					page.getMeta().put("img", pageMap.get("img"));

				if (status.getRet() == 0) {
					page.setId(TableUtil.IdReverse(status.getRetValue()));
					if (rsIds[0].equals("0"))
						arClient.setArticleCover(status.getRetValue(), rsIds[2]);

					itemService.put(pageMap.get("biz_code"), TableUtil.IdReverse(status.getRetValue()), page);
					long time4 = System.currentTimeMillis() - begin;

					LOG.info("爬取数据成功:["+biz_code+"]<title:" + page.getTitle() + "><url:" + ogUrl + "><catagory:" + catagory
							+ "><CostTime:<prepare:" + time1 + "><upLoadFile:" + time2 + "><toCms:" + time3
							+ "><toHbase:" + time4 + "><Total:" + (time1 + time2 + time3 + time4) + ">>");
					
//					if(biz_code.equals("govheadlines")){
//						LOG.info("MSG:"+gson.toJson(ar));
//					}
					return "success";
				} else {
					LOG.error("存储cms异常:[title:" + pageMap.get("title") + "   url:" + pageMap.get("baseUrl") + "::<"
							+ status.getMsg() + ">]");

					return "fail:";

				}

			} catch (NullPointerException e) {

				LOG.error("日志采集失败:[title:" + pageMap.get("title") + "   url:" + pageMap.get("baseUrl") + "::<" + e
						+ ">>>类别：" + catagory + ">]");
				e.printStackTrace();
				return "fail";
			} catch (Exception e) {
				//
				LOG.error("日志采集失败:[title:" + pageMap.get("title") + "   url:" + pageMap.get("baseUrl") + "::<"
						+ e.getMessage() + ">]");

				e.printStackTrace();

				return "fail";
			}

		}
		LOG.error("日志采集失败:不明原因[title:" + pageMap.get("title") + "   url:" + pageMap.get("baseUrl") + "::<"
				+ pageMap.get("catagory") + ">]");
		return "fail";

	}

}
