package com.lx.leyou.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("springboot-provider")
public interface HelloClient {
    @RequestMapping("hello")
    String hello();


}
