package com.bza.tennisranking.config;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

//import com.bza.tennisranking.config.Configurations.SwtConfigurationProperties;
import com.bza.tennisranking.config.TestConfiguration.SwtConfigurationProperties;
import com.bza.tennisranking.httpClient.SwtApplicationClient;


@Configuration
@Profile("test")
@EnableConfigurationProperties(SwtConfigurationProperties.class)
 public class TestConfiguration {
		@PostConstruct
		public void init() {
		}

		protected static class SwtApplicationClientConfiguration {
			@Bean
			public SwtApplicationClient applicationClient(SwtConfigurationProperties properties) {
				return new SwtApplicationClient(properties.getUrlTemplate(), properties.getDirectory(), properties.getDirectory2());
				
			}
		}
		
		@ConfigurationProperties(prefix = "swisstennis")
		@PropertySource("classpath:configprops.properties")
		public static class SwtConfigurationProperties {
			//@NotNull
			private String urlTemplate;
			//@NotNull
			private String directory;
			
			public String getDirectory2() {
				return directory2;
			}
			public void setDirectory2(String directory2) {
				this.directory2 = directory2;
			}
			private String directory2;
			
			public String getDirectory() { return directory;}
			public void setDirectory(String directory) { this.directory = directory;}
			public String getUrlTemplate() {return urlTemplate;}
			public void setUrlTemplate(String urlTemplate) { this.urlTemplate = urlTemplate;
			}
			
		}
		
}
