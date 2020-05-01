package com.cloud;

import com.cloud.system.annotation.EnableRyFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author xuxueli 2018-10-28 00:38:13
 */
@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
@EnableRyFeignClients
public class CloudJobExecutorApplication {

	public static void main(String[] args) {
        SpringApplication.run(CloudJobExecutorApplication.class, args);
	}

}
