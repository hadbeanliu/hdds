package com.linghua.hdds.api.conf;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.linghua.hdds.api.db.HbaseDaoImpl;
import com.rongji.cms.webservice.client.json.CmsClientFactory;

@Configuration
@ConfigurationProperties(prefix="cms")
public class FactoryConfigAndReleaseConf {

	@Value("${cms.url}")
	private String url;
	@Value("${cms.client.id}")
	private String clientId;
	@Value("${cms.client.token}")
	private String clientToken;
	

	@Bean
	public CmsClientFactory getCmsClient() {

		return new CmsClientFactory(url, clientId, clientToken);
	}

	@Bean
	public HbaseDaoImpl getHTImpl(){
		
		return new HbaseDaoImpl(HBaseConfiguration.create());
	}
	@Bean
	public TemporaryRecorder getTempRecorder(){

		return new TemporaryRecorder();
	}
	@Bean
	public ItemRecorder getItemRecorder(){

		return new ItemRecorder();
	}
	

}
