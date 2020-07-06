package com.lx.api;

import com.lx.entity.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CategoryApi {

    /**
     *根据父节点查询子节点
     * @param pid 父节点
     * @return 子节点
     */
    @GetMapping("/category/list")
    List<Category> queryCategoryById(
            @RequestParam(value = "pid", defaultValue = "0") Long pid);


    /**
     * 根据商品分类id，查询商品分类名称
     * @param ids 分类id
     * @return 分类名称
     */
    @GetMapping("/category/names")
    List<String> queryNameByIds(@RequestParam("ids") List<Long> ids);
}
