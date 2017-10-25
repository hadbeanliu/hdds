package com.linghua.hdds.api.response;

public interface VoExchange<ID,T> {

	
	public void exchange(T obj);

	public VoExchange to(T obj);
	
}
