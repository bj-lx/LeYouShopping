package com.lx.leyou.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lx.entity.Category;
import com.lx.leyou.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    public CategoryMapper categoryMapper;

    /**
     * 根据父节点查询子节点
     * @param pid 父节点
     * @return 子节点
     */
    public List<Category> queryCategoryById(Long pid) {
        QueryWrapper<Category> qw = new QueryWrapper<>();
        qw.eq("parent_id", pid);
        return this.categoryMapper.selectList(qw);
    }

    /**
     * 根据分类id查询所有分类名称
     * @param ids 分类id集合
     * @return 所有分类名称
     */
    public List<String> queryCategoryNameList(List<Long> ids) {
        List<Category> categories = this.categoryMapper.selectBatchIds(ids);
        return categories.stream().map(Category::getName).collect(Collectors.toList());
    }

    /**
     * 根据商品分类id集合，查询商品分类名称
     * @param ids 分类id集合
     * @return 分类名称
     */
    public List<String> queryNameByIds(List<Long> ids) {
        List<String> names = new ArrayList<>();
        this.categoryMapper.selectBatchIds(ids).forEach(category -> {
            names.add(category.getName());
        });
        return names;
    }

    /**
     * 根据分类名称查询三级分类
     * @param name 分类名称
     * @return 三级分类
     */
    public List<Category> queryCategoryByName(Long name) {
        Category category3 = this.categoryMapper.selectById(name);
        Category category2 = this.categoryMapper.selectById(category3.getParentId());
        Category category1 = this.categoryMapper.selectById(category2.getParentId());

        return Arrays.asList(category1, category2, category3);
    }
}
