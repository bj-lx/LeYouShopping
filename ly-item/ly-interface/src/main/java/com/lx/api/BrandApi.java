package com.lx.api;

import com.lx.entity.Brand;
import com.lx.leyou.entity.PageResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/brand")
public interface BrandApi {

    /**
     * 根据查询条件查询结果并分页
     *
     * @param key    查询条件
     * @param page   当前页码
     * @param rows   每页条数
     * @param sortBy 排序字段
     * @param desc   排序规则
     * @return 分页结果
     */
    @GetMapping("/page")
    PageResult<Brand> queryListByLike(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", defaultValue = "true") boolean desc);

    /**
     * 根据分类id查询品牌
     *
     * @param cid 分类id
     * @return 品牌
     */
    @GetMapping("/cid/{cid}")
    List<Brand> queryBrandList(@PathVariable("cid") Long cid);

    /**
     * 根据商品品牌id，查询商品的品牌
     *
     * @param brandId 品牌id
     * @return 品牌
     */
    @GetMapping("/{brandId}")
    Brand queryBrandById(@PathVariable("brandId") Long brandId);

}

