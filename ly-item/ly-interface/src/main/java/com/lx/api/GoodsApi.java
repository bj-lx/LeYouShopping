package com.lx.api;

import com.lx.bo.SpuBo;
import com.lx.entity.Sku;
import com.lx.entity.Spu;
import com.lx.entity.SpuDetail;
import com.lx.leyou.entity.PageResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


public interface GoodsApi {

    /**
     * 根据搜索条件和上下架情况分页查询商品列表
     * @param key 搜索条件
     * @param saleable 上下架情况
     * @param page 当前页码
     * @param rows 每页记录数
     * @return 商品列表
     */
    @GetMapping("/spu/page")
    PageResult<SpuBo> queryGoods(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows);

    /**
     * 根据spuId查询spuDetail
     * @param spuId spuId
     * @return spuDetail
     */
    @GetMapping("/spu/detail/{spuId}")
    SpuDetail querySpuDetailById(@PathVariable("spuId") Long spuId);


    /**
     * 根据spuId查询sku集合
     * @param id spuId
     * @return sku集合
     */
    @GetMapping("/sku/list")
    List<Sku> querySkuListById(@RequestParam Long id);

    /**
     * 根据spuId查询spu
     * @param spuId spuId
     * @return spu
     */
    @GetMapping("{spuId}")
    Spu querySpuById(@PathVariable("spuId") Long spuId);

    /**
     * 根据skuId查询sku
     * @param skuId skuId
     * @return sku
     */
    @GetMapping("/sku/{skuId}")
    Sku querySkuBySkuId(@PathVariable("skuId") Long skuId);
}
