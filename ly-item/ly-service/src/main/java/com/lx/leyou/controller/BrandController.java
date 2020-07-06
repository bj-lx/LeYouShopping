package com.lx.leyou.controller;

import com.lx.entity.Brand;
import com.lx.leyou.entity.PageResult;
import com.lx.leyou.service.BrandService;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Controller
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    public BrandService brandService;

    /**
     * 根据查询条件查询结果并分页
     * @param key 查询条件
     * @param page 当前页码
     * @param rows 每页条数
     * @param sortBy 排序字段
     * @param desc 排序规则
     * @return 分页结果
     */
    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryListByLike(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", defaultValue = "true") boolean desc) {
        PageResult<Brand> brandPage = this.brandService.queryListByLike(key, page, rows, sortBy, desc);
        if (CollectionUtils.isEmpty(brandPage.getItems())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brandPage);
    }

    /**
     * 添加品牌
     * @param brand 品牌
     * @param cids 对应的分类
     * @return 是否成功
     */
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("cids")List<Long> cids) {
        this.brandService.insertBrandAndCategory(brand, cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据分类id查询品牌
     * @param cid 分类id
     * @return 品牌
     */
    @GetMapping("/cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandList(@PathVariable("cid") Long cid) {
        List<Brand> brands = this.brandService.queryBrandList(cid);
        if (CollectionUtils.isEmpty(brands)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brands);
    }

    /**
     * 根据商品品牌id，查询商品的品牌
     * @param brandId 品牌id
     * @return 品牌
     */
    @GetMapping("{brandId}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("brandId") Long brandId) {
        Brand brand = this.brandService.queryBrandById(brandId);
        if (brand == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brand);
    }
}
