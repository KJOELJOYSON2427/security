package com.sivalabs.messages;

import com.sivalabs.messages.Security.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
//@ConfigurationPropertiesScan(basePackages = "com.sivalabs.messages.Security")
@EnableConfigurationProperties(AppProperties.class)
@EnableFeignClients(basePackages = "com.sivalabs.messages.feign")
public class MessagesWebappApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessagesWebappApplication.class, args);
	}

}
