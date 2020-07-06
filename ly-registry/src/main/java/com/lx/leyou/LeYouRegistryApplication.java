package com.lx.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class LeYouRegistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeYouRegistryApplication.class);
    }
}
