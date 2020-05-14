package com.cloud;

import com.cloud.system.annotation.EnableRyFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 启动程序
 *
 * @author cloud
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@EnableRyFeignClients
@MapperScan("com.cloud.*.mapper")
public class CloudSettleApplication {
    public static void main(String[] args) {
        //   System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(CloudSettleApplication.class, args);
    }
}
