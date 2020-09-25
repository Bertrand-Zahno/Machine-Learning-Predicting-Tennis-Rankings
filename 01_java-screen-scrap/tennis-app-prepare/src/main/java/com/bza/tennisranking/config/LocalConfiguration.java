package com.bza.tennisranking.config;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

//import com.bza.tennisranking.config.Configurations.SwtConfigurationProperties;
import com.bza.tennisranking.config.LocalConfiguration.SwtConfigurationProperties;
import com.bza.tennisranking.httpClient.SwtApplicationClient;


@Configuration
@Profile("local")
@EnableConfigurationProperties(SwtConfigurationProperties.class)
 public class LocalConfiguration {
		@PostConstruct
		public void init() {
		}

		protected static class SwtApplicationClientConfiguration {
			@Bean
			public SwtApplicationClient applicationClient(SwtConfigurationProperties properties) {
				return new SwtApplicationClient(properties.getUrlTemplate(), properties.getDirectory());
				
			}
		}
		
		@ConfigurationProperties(prefix = "swisstennis")
		@PropertySource("classpath:configprops.properties")
		public static class SwtConfigurationProperties {
			//@NotNull
			private String urlTemplate;
			//@NotNull
			private String directory;
			
			public String getDirectory() { return directory;}
			public void setDirectory(String directory) { this.directory = directory;}
			public String getUrlTemplate() {return urlTemplate;}
			public void setUrlTemplate(String urlTemplate) { this.urlTemplate = urlTemplate;
			}
			
		}
		
}
