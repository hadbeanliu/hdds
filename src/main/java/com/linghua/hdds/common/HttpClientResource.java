package com.linghua.hdds.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

public class HttpClientResource {
	private static enum MethodSignal {
		PUT, GET, POST
	}

	public static String doSend(String uri,Map<String, String> queue,String METHOD){
		String msg="";
		HttpClient client=new HttpClient();
		
		HttpMethod method=null;
		MethodSignal met = null;
		try{
			met = Enum.valueOf(MethodSignal.class, METHOD.toUpperCase().trim());
		}catch(Exception e){
			throw new IllegalArgumentException("unknow request method:"+METHOD.toUpperCase().trim());
		}
		switch(met){
			
			case GET:method=new GetMethod(uri);break;
			case POST:method=new PostMethod(uri);break;
			case PUT:method=new PutMethod(uri);break;
		}
		
		if(queue!=null&&!queue.isEmpty()){
			NameValuePair[] data=new NameValuePair[queue.size()];
			int i=0;
 			for(Entry<String, String> kv:queue.entrySet()){
					data[i]=new NameValuePair(kv.getKey(), kv.getValue());
				
 				i++;
 			}
 			
			if(MethodSignal.POST.equals(met)){
				((PostMethod)method).setRequestBody(data);
			}
			else{
				method.setQueryString(data);
			}
		}
	
		try {
			int status=client.executeMethod(method);
			
			if(status!=HttpStatus.SC_OK)
				throw new RuntimeException("server error!"+status);
	
			msg=method.getResponseBodyAsString();
			
		}catch (HttpException e){
			System.err.println("Fatal protocol violation: " + e.getMessage());
		      e.printStackTrace();
		}catch (IOException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
		      e.printStackTrace();
		}catch (Exception e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
		      e.printStackTrace();
		}finally{
			method.releaseConnection();
		}
		return msg;
	}
	
	public static String post(Object entity,String uri){
		
		ClientResource client=new ClientResource(uri);
		Representation res=null;
		if(entity!=null)
		 res= client.post(entity);
		else res =client.get();
		
		 try {
			
			 String result=res.getText();
			if(result==null)
				 throw new NullPointerException( "自动分类系统出现异常！"+result );
			return result;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
			client.release();
			
		}
		 
		 
		
//		method.set
		return null;
	}

	/**
	 * @param args
	 * @throws URIException 
	 * @throws UnsupportedEncodingException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws URIException, UnsupportedEncodingException, FileNotFoundException {

		HttpClientResource client=new HttpClientResource();
		
		String result=client.doSend("http://www.wenku8.com/novel/1/1973/80316.htm", null, "get");
		String encode=new String(result.getBytes("ISO8859-1"),"GBK");
		
//		encode=HtmlParser.getTextFromHtml(encode);
//		System.out.println(HtmlParser.getTextFromHtml(encode));
//		Pattern p=Pattern.compile("<a href=\"(.*).html[\\s\\S].*>下一页</a>");
//		Matcher matcher=p.matcher(encode);
//
//		System.out.println("--------------");
//		
//		
//		String[] r= encode.replaceAll("。", "\n").split("\n");
//		for(String s:r){
//			
//			if(s.length()>200){
//				System.out.println(s.replaceAll("</>","").replaceAll("&;&;&;&;", "").replaceAll("，|,", ",\n\n"));
//				
//			}
//		}
//		
//		while(matcher.find()){
//			System.out.println(matcher.group(1));
//		}
//
		
		System.out.println(encode.replaceAll("。", "\n"));
		
	}
	
	
}
