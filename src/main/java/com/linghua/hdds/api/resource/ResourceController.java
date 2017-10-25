package com.linghua.hdds.api.resource;

import java.io.UnsupportedEncodingException;

import javax.ws.rs.QueryParam;

import com.linghua.hdds.common.CataLogManager;
import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.DictionaryFactory;
import org.lionsoul.jcseg.tokenizer.core.ILexicon;
import org.lionsoul.jcseg.tokenizer.core.IWord;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.linghua.hdds.common.MainWordExtractor;

@RestController
@RequestMapping("/resource")
public class ResourceController {

	private static Gson gson = new Gson();

	@RequestMapping("/getWord")
	@ResponseBody
	public String getSplitWord(@QueryParam(value = "word") String word) {
		ADictionary dic = DictionaryFactory.createSingletonDictionary(null, false);
		Assert.hasLength(word, "word cant be null");
		
		IWord iword = dic.get(ILexicon.CJK_WORD, word);

		if (iword == null)
			iword = dic.get(ILexicon.STOP_WORD, word);
		if (iword == null)
			iword = dic.get(ILexicon.CJK_CHAR, word);
		if (iword == null)
			iword = dic.get(ILexicon.CJK_UNIT, word);

		return gson.toJson(iword);
	}

	@RequestMapping("/catalog2/{biz}")
	public String getCaNameAndId(@PathVariable String biz){
        return new Gson().toJson(CataLogManager.getAllCatalog(biz));
    }

	@RequestMapping("/speechpart")
	@ResponseBody
	public String speechPart(@QueryParam(value = "word") String word, @QueryParam(value = "speech") String speech,
			@QueryParam(value = "type") String type) {

		Assert.hasText(word, "word cant be null");

		int tp = ILexicon.CJK_WORD;
		try {

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
			MainWordExtractor extractor = MainWordExtractor.getInstance();

			System.out.println("speech:::" + speech);
			word = word.trim().replace("\n", "").toLowerCase();
			extractor.addIWord(speech, tp, word, true);

			extractor.exportToLocal(speech, tp, word);
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}

		return "success";
	}

}
