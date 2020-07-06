package com.lx.leyou.controller;

import com.lx.bo.SpuBo;
import com.lx.entity.Sku;
import com.lx.entity.Spu;
import com.lx.entity.SpuDetail;
import com.lx.leyou.entity.PageResult;
import com.lx.leyou.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class GoodsController {

    @Autowired
    public GoodsService goodsService;

    @ResponseBody
    @RequestMapping("/hello")
    public String hello() {
        return "hello";
    }
    /**
     * 根据搜索条件和上下架情况分页查询商品列表
     * @param key 搜索条件
     * @param saleable 上下架情况
     * @param page 当前页码
     * @param rows 每页记录数
     * @return 商品列表
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuBo>> queryGoods(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows) {

        System.out.println("进入查询商品列表操作！");
        PageResult<SpuBo> spuBos = this.goodsService.queryGoods(key, saleable, page, rows);

        if (spuBos == null || CollectionUtils.isEmpty(spuBos.getItems())) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(spuBos);
    }

    /**
     * 新增商品
     * @param spuBo 商品信息
     * @return 状态码
     */
    @PostMapping("goods")
    public ResponseEntity<Void> saveGood(@RequestBody SpuBo spuBo) {
        this.goodsService.saveGood(spuBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改商品
     * @param spuBo 商品信息
     * @return 无
     */
    @PutMapping("goods")
    public ResponseEntity<Void> updateGood(@RequestBody SpuBo spuBo) {
        this.goodsService.updateGood(spuBo);
        return ResponseEntity.noContent().build();
    }


    /**
     * 根据spuId查询spuDetail
     * @param spuId spuId
     * @return spuDetail
     */
    @GetMapping("/spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> querySpuDetailById(@PathVariable("spuId") Long spuId) {
        SpuDetail spuDetail = this.goodsService.querySpuDetailById(spuId);
        if (spuDetail == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(spuDetail);
    }

    /**
     * 根据spuId查询sku集合
     * @param id spuId
     * @return sku集合
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> querySkuListById(@RequestParam Long id) {
        List<Sku> skus = this.goodsService.querySkuListById(id);
        if (CollectionUtils.isEmpty(skus)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(skus);
    }

    /**
     * 根据spuId查询spu
     * @param spuId spuId
     * @return spu
     */
    @GetMapping("{spuId}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("spuId") Long spuId) {
        Spu spu = this.goodsService.querySpuById(spuId);
        if (spu == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(spu);
    }

    /**
     * 根据skuId查询sku
     * @param skuId skuId
     * @return sku
     */
    @GetMapping("/sku/{skuId}")
    public ResponseEntity<Sku> querySkuBySkuId(@PathVariable("skuId") Long skuId) {
        Sku sku = this.goodsService.querySkuBySkuId(skuId);
        if (sku == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(sku);
    }

}
