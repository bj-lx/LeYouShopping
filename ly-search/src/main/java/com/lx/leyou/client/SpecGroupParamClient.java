package com.lx.leyou.client;

import com.lx.api.SpecGroupParamApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("ITEM-SERVICE")
public interface SpecGroupParamClient extends SpecGroupParamApi {

}
