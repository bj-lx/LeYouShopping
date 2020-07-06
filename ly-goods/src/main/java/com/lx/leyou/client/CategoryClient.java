package com.lx.leyou.client;

import com.lx.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("ITEM-SERVICE")
public interface CategoryClient extends CategoryApi {

}
