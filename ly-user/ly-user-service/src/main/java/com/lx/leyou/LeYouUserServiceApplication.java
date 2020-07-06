package com.lx.leyou;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan
public class LeYouUserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeYouUserServiceApplication.class);
    }
}
