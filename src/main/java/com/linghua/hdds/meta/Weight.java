package com.linghua.hdds.meta;

public class Weight {
	
	private static final Weight WEIGHT =new Weight(0d,0d);
	
	private String word;
	private Double idf;
	private Double tfidf;
	
	
	
	public Weight() {
		super();
	}
	public Weight(Double idf, Double tfidf) {
		super();
		this.idf = idf;
		this.tfidf = tfidf;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public Double getIdf() {
		return idf;
	}
	public void setIdf(Double idf) {
		this.idf = idf;
	}
	public Double getTfidf() {
		return tfidf;
	}
	public void setTfidf(Double tfidf) {
		this.tfidf = tfidf;
	}
	
	public static Weight getZero(){return WEIGHT;}
	
}
