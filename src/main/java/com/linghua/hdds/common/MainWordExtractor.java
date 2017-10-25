package com.linghua.hdds.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

import org.lionsoul.jcseg.extractor.impl.TextRankKeywordsExtractor;
import org.lionsoul.jcseg.tokenizer.Word;
import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.DictionaryFactory;
import org.lionsoul.jcseg.tokenizer.core.ILexicon;
import org.lionsoul.jcseg.tokenizer.core.ISegment;
import org.lionsoul.jcseg.tokenizer.core.IWord;
import org.lionsoul.jcseg.tokenizer.core.JcsegException;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;
import org.lionsoul.jcseg.tokenizer.core.SegmentFactory;

import com.google.gson.Gson;
import com.linghua.hdds.meta.TwoTuple;

public class MainWordExtractor {

    private static String WORD_DEFEND_PATH = "/home/hadoop/train";

    private static MainWordExtractor executor;

    private static JcsegTaskConfig tokenizerConfig = null;
    ADictionary dic = null;

    public static MainWordExtractor getInstance() {

        if (executor == null)
            executor = new MainWordExtractor();

        return executor;
    }

    private MainWordExtractor() {
        tokenizerConfig = new JcsegTaskConfig(true);

        try {

            JcsegTaskConfig extractorConfig = tokenizerConfig.clone();
            dic = DictionaryFactory.createSingletonDictionary(tokenizerConfig);
            // two ways to reload lexicons
            // for ( String lpath : config.getLexiconPath() )
            // dic.loadDirectory(lpath);
            // dic.load("/java/lex-main.lex");
//			tokenizerSeg = SegmentFactory.createJcseg(JcsegTaskConfig.NLP_MODE,
//					new Object[] { tokenizerConfig, dic });

            // segmentation object for extractor
//			extractorConfig.setAppendCJKPinyin(false);
//			extractorConfig.setClearStopwords(true);
//			extractorConfig.setKeepUnregWords(false);

//			keywordsExtractor = new TextRankKeywordsExtractor(tokenizerSeg);
//			keyphraseExtractor = new TextRankKeyphraseExtractor(tokenizerSeg);
//			summaryExtractor = new TextRankSummaryExtractor(tokenizerSeg, new SentenceSeg());
//
//			TextRankKeyphraseExtractor trkp = (TextRankKeyphraseExtractor) keyphraseExtractor;
//			trkp.setAutoMinLength(4);
//			trkp.setMaxWordsNum(4);

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        tokenizerConfig.EN_SECOND_SEG = false;
        tokenizerConfig.CNNUM_TO_ARABIC = false;
        // config.setAppendCJKPinyin(true);
        System.out.println("jcseg参数设置：");
        System.out.println("当前加载的配置文件：" + tokenizerConfig.getPropertieFile());
        System.out.println("最大切分匹配词数：" + tokenizerConfig.MAX_LENGTH);
        System.out.println("开启中文人名识别：" + tokenizerConfig.I_CN_NAME);
        System.out.println("最大姓氏前缀修饰：" + tokenizerConfig.MAX_CN_LNADRON);
        System.out.println("最大标点配对词长：" + tokenizerConfig.PPT_MAX_LENGTH);
        System.out.println("词库词条拼音加载：" + tokenizerConfig.LOAD_CJK_PINYIN);
        System.out.println("分词词条拼音追加：" + tokenizerConfig.APPEND_CJK_PINYIN);
        System.out.println("词库同义词的载入：" + tokenizerConfig.LOAD_CJK_SYN);
        System.out.println("分词同义词的追加：" + tokenizerConfig.APPEND_CJK_SYN);
        System.out.println("词库词条词性载入：" + tokenizerConfig.LOAD_CJK_POS);
        System.out.println("去除切分后噪音词：" + tokenizerConfig.CLEAR_STOPWORD);
        System.out.println("中文数字转阿拉伯：" + tokenizerConfig.CNNUM_TO_ARABIC);
        System.out.println("中文分数转阿拉伯：" + tokenizerConfig.CNFRA_TO_ARABIC);
        System.out.println("保留未识别的字符：" + tokenizerConfig.KEEP_UNREG_WORDS);
        System.out.println("英文词条二次切分：" + tokenizerConfig.EN_SECOND_SEG);
        System.out.println("姓名成词歧义阕值：" + tokenizerConfig.NAME_SINGLE_THRESHOLD + "\n");
        tokenizerConfig.EN_SECOND_SEG = false;

    }


    public boolean addIWord(String speech, int t, String key, boolean force) {
        synchronized (dic) {
            if (dic == null)
                throw new NullPointerException("dic has not init");
            if (t == -1) {
                dic.remove(ILexicon.CJK_WORD, key);
                return true;
            }
            IWord word = new Word(key, t);
            word.setPartSpeech(speech.split(","));
            ;
            if (force) {
                dic.remove(t, key);
                dic.add(t, word);
            } else
                dic.add(t, word);

        }

        return true;

    }

    public IWord getWord(String key, int type) {

        return dic.get(ILexicon.CJK_WORD, key);

    }

    public void exportToLocal(String speech, int t, String word) {
        String path = tokenizerConfig.getLexiconPath()[0];

        if (path == null)
            path = WORD_DEFEND_PATH;

        if (t == -1) {
            File f = new File("/home/hadoop/train/lex-main.lex");

            String[] files = tokenizerConfig.getLexiconPath();

            for (String dir : files) {
                removeWordFromLocal(word, dir + "/lex-main.lex");
            }
            dic.remove(ILexicon.CJK_WORD, word);

            return;

        }


//		File file=new File(path);
        String fileName = "";
        String type = "CJK_WORD";
        switch (t) {
            case ILexicon.CJK_WORD:
                fileName = "lex-main.lex";
                break;
            case ILexicon.STOP_WORD:
                fileName = "lex-stopWord.lex";
                type = "STOP_WORD";
                break;
            case ILexicon.CJK_UNIT:
                fileName = "lex-unit.lex";
                type = "CJK_UNIT";
                break;
            case ILexicon.CJK_CHAR:
                fileName = "lex-chars.lex";
                type = "CJK_CHAR";
                break;
            default:
                throw new IllegalArgumentException("unknow type of word");
        }
//		LocalDate now=LocalDate.now();
        path = path + "/" + fileName;

        System.out.println("saved word :" + word);

        File file = new File(path);

        try {
            if (file.exists()) {
                System.out.println("save in no exists");
                FileOutputStream out = new FileOutputStream(file, true);
                StringBuffer line = new StringBuffer();
                line.append("\n").append(word).append("/").append(speech).append("/null/null");
                out.write(line.toString().getBytes());
                out.flush();
                out.close();
            } else {
                System.out.println("save in exists");
                FileOutputStream out = new FileOutputStream(file, true);
                StringBuffer line = new StringBuffer();
                line.append(type).append("\n").append(word).append("/").append(speech).append("/null/null");
                out.write(line.toString().getBytes());
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    public List<TwoTuple<String, String>> simpleTokenizeWithPart(String str) throws JcsegException, IOException {

        if(str ==null)
            return null;
        List<TwoTuple<String, String>> result = new ArrayList<TwoTuple<String, String>>();
        IWord word = null;
        ISegment newTokenizerSeg = SegmentFactory.createJcseg(JcsegTaskConfig.COMPLEX_MODE,
                new Object[]{tokenizerConfig, dic});
        newTokenizerSeg.reset(new StringReader(str));
        while ((word = newTokenizerSeg.next()) != null) {
//			if (word.getPartSpeech() == null)
//				continue;
//			String speech=word.getPartSpeech()[0];

//			if(dic.get(ILexicon.CJK_WORD, word.getValue())!=null||speech.equals("en"))
            result.add(new TwoTuple<String, String>(word.getValue(), word.getPartSpeech() == null ? null : word.getPartSpeech()[0]));
        }

        return result;

    }
    public Map<String, String> tokenizeWithPart(String str) throws JcsegException, IOException {

        Map<String, String> result = new LinkedHashMap<>();
        IWord word = null;
        ISegment newTokenizerSeg = SegmentFactory.createJcseg(JcsegTaskConfig.COMPLEX_MODE,
                new Object[]{tokenizerConfig, dic});
        newTokenizerSeg.reset(new StringReader(str));
        while ((word = newTokenizerSeg.next()) != null) {
//			if (word.getPartSpeech() == null)
//				continue;
//			String speech=word.getPartSpeech()[0];

//			if("n".equals(speech)||"en".equals(speech))
              result.put(word.getValue(), word.getPartSpeech() == null ? null : word.getPartSpeech()[0]);
        }

        return result;

    }

    public List<String> simpleTokenize(String str) throws IOException, JcsegException {

        List<String> result = new ArrayList<String>();
        IWord word = null;
        ISegment newTokenizerSeg = SegmentFactory.createJcseg(JcsegTaskConfig.NLP_MODE,
                new Object[]{tokenizerConfig, dic});
        newTokenizerSeg.reset(new StringReader(str));
        while ((word = newTokenizerSeg.next()) != null) {
//			if (word.getPartSpeech() == null)
//				continue;
//			String speech=word.getPartSpeech()[0];

//			if(dic.get(ILexicon.CJK_WORD, word.getValue())!=null||speech.equals("en"))
            result.add(word.getValue());
        }

        return result;

    }

    public Map<String, String> tokenize(String str) throws IOException, JcsegException {

        Map<String, String> result = new HashMap<String, String>();
        IWord word = null;
        ISegment newTokenizerSeg = SegmentFactory.createJcseg(JcsegTaskConfig.COMPLEX_MODE,
                new Object[]{tokenizerConfig, dic});
        newTokenizerSeg.reset(new StringReader(str));

        while ((word = newTokenizerSeg.next()) != null) {
            if (word.getPartSpeech() == null)
                continue;

            String key = "nt";
            String t = word.getPartSpeech()[0];

            if (t.startsWith("ns"))
                key = "ns";
            else if (t.startsWith("nr"))
                key = "nr";
            else if (t.startsWith("nt")) {

            } else if (t.startsWith("np")) {
                key = "np";
            } else continue;

            if (dic.get(ILexicon.CJK_WORD, word.getValue()) == null)
                continue;

            if (result.get(key) == null) {
                result.put(key, word.getValue());
            } else {
                if (result.get(key).indexOf(word.getValue()) == -1)
                    result.put(key, word.getValue() + "," + result.get(key));
            }
        }

        return result;
    }

    int windowSize = 4;
    protected int maxIterateNum = 20;
    Float D = 0.85F;

    public List<Map.Entry<String,Float>> textRank(List<String> words, int topN) throws JcsegException, IOException {
        Map<String, List<String>> winMap = new HashMap<String, List<String>>();



        IWord W = null;
        for (String w : words) {
            if (!winMap.containsKey(w)) {
                winMap.put(w, new LinkedList<String>());
            }
        }
        for (int i = 0; i < words.size(); i++) {

            String word = words.get(i);
            List<String> support = winMap.get(word);

            int sIdx = Math.max(0, i - windowSize);
            int eIdx = Math.min(i + windowSize, words.size() - 1);

            for (int j = sIdx; j <= eIdx; j++) {
                if (j != i)
                    support.add(words.get(i));
            }

        }

        HashMap<String, Float> score = new HashMap<String, Float>();

        for (int c = 0; c < maxIterateNum; c++) {

            for (Map.Entry<String, List<String>> entry : winMap.entrySet()) {
                String key = entry.getKey();
                List<String> value = entry.getValue();
                Float sigema = 0F;
                for (String ele : value) {
                    if (ele.equals(key) || winMap.get(ele).size() == 0)
                        continue;

                    int size = winMap.get(ele).size();

                    Float Sy = 0f;
                    if (score != null
                            && score.containsKey(ele)) {
                        Sy = score.get(ele);
                    }

                    sigema += Sy / size;
                }

                score.put(key, 1 - D + D * sigema);
            }
        }
        return score.entrySet().stream().sorted((x, y)->x.getValue().compareTo(y.getValue())).collect(Collectors.toList());
    }


	public List<String> tokenizeWithoutPart(String str) throws IOException, JcsegException {

		if(str ==null)
			return new ArrayList<String>();

		List<String> result = new ArrayList<String>();
		IWord word = null;
		ISegment newTokenizerSeg = SegmentFactory.createJcseg(JcsegTaskConfig.COMPLEX_MODE,
				new Object[] { tokenizerConfig, dic });
		newTokenizerSeg.reset(new StringReader(str));
		
		while ((word = newTokenizerSeg.next()) != null) {
			if (word.getPartSpeech() == null)
				continue;
			
			String key = "nz";
			String t = word.getPartSpeech()[0];		
			if(t.startsWith("n") || t.equals("en") && word.getValue().length() > 1)
			   result.add(word.getValue());
		}
       
		return result;

	}
	
	private boolean removeWordFromLocal(String word,String file){
		File f=new File(file);
		if(!f.exists())
			return false;
		BufferedReader read=null;
		PrintWriter write=null;
		try {
			boolean flag=false;
			read=new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			StringBuffer buff=new StringBuffer();
			String tmp="";
			buff.append(read.readLine());
			
			while((tmp=read.readLine())!=null){
				if(tmp.startsWith("#")){
					buff.append("\n").append(tmp);
					continue;
				}
				if(tmp.startsWith(word+"/")){ flag=true; System.out.println("delete word:"+word+" from file:"+f);  continue;}
				buff.append("\n").append(tmp);
				
			}
			if(flag){
			write=new PrintWriter(f);
			write.write(buff.toString());
			write.flush();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				read.close();
				if(write!=null)
				   write.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		return false;
	}

	public static void main(String[] args) {
		
		System.out.println("is true"+"nrst".startsWith("nr|ns"));
		
		String str = "确保在六月底前签约率达到 100%。</p><p><strong>二、打好决胜之仗";
		MainWordExtractor extractor = new MainWordExtractor();

		List<String> rs;
		try {
			rs = extractor.simpleTokenize(str);
			System.out.println(new Gson().toJson(rs));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JcsegException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
