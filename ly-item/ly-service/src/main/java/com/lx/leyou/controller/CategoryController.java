package com.lx.leyou.controller;

import com.lx.entity.Category;
import com.lx.leyou.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     *根据父节点查询子节点
     * @param pid 父节点
     * @return 子节点
     */
    @GetMapping("/list")
    public ResponseEntity<List<Category>> queryCategoryById(
            @RequestParam(value = "pid", defaultValue = "0") Long pid) {
        if (pid == null || pid < 0) {
            //400参数不合法
            return ResponseEntity.badRequest().build();
        }
        List<Category> categories = this.categoryService.queryCategoryById(pid);
        if (CollectionUtils.isEmpty(categories)) {
            //404服务器资源未找到
            return ResponseEntity.notFound().build();
        }

        //200查询成功
        return ResponseEntity.ok(categories);
    }

    /**
     * 根据商品分类集合，查询商品分类名称
     * @param ids 分类id集合
     * @return 分类名称
     */
    @GetMapping("names")
    ResponseEntity<List<String>> queryNameByIds(@RequestParam("ids") List<Long> ids) {
        List<String> names = this.categoryService.queryNameByIds(ids);
        if (CollectionUtils.isEmpty(names)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(names);
    }

    /**
     * 根据分类名称查询三级分类
     * @param name 分类名称
     * @return 三级分类
     */
    @GetMapping("all/level")
    ResponseEntity<List<Category>> queryCategoryByName(@RequestParam("id") Long name) {
        List<Category> categories = this.categoryService.queryCategoryByName(name);
        if (CollectionUtils.isEmpty(categories)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categories);
    }

}
