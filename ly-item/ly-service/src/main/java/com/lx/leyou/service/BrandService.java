package com.lx.leyou.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.entity.Brand;
import com.lx.leyou.entity.PageResult;
import com.lx.leyou.mapper.BrandMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class BrandService {

    @Autowired
    public BrandMapper brandMapper;

    /**
     * 根据查询条件查询结果并分页
     * @param key 查询条件
     * @param page 当前页码
     * @param rows 每页条数
     * @param sortBy 排序字段
     * @param desc 排序规则
     * @return 分页结果
     */
    public PageResult<Brand> queryListByLike(String key, Integer page, Integer rows, String sortBy, boolean desc) {
        QueryWrapper<Brand> qw = new QueryWrapper<>();
        IPage<Brand> brandPage = new Page<>();
        brandPage.setSize(rows);
        brandPage.setCurrent(page);
        qw.like("name", key);
        qw.orderBy(true, desc, sortBy);

        IPage<Brand> brandIPage = this.brandMapper.selectPage(brandPage, qw);
        PageResult<Brand> pageResult= new PageResult<>();
        pageResult.setItems(brandIPage.getRecords());
        pageResult.setTotal(brandIPage.getTotal());
        pageResult.setTotalPage((int) brandPage.getPages());

        return pageResult;
    }


    /**
     * 添加品牌
     * @param brand 品牌
     * @param cids 对应的分类
     */
    @Transactional
    public void insertBrandAndCategory(Brand brand, List<Long> cids) {

        //添加brand
        boolean insert = brand.insert();
        //添加brand中间表值
        if (insert) {
            cids.forEach(cid -> {
                this.brandMapper.insertBrandAndCategory(brand.getId(), cid);
            });
        }
    }

    /**
     * 根据品牌id查询品牌
     * @param brandId 品牌id
     * @return 品牌
     */
    public Brand queryBrandById(Long brandId) {
        return this.brandMapper.selectById(brandId);
    }

    /**
     * 根据分类id查询品牌
     * @param cid 分类id
     * @return 品牌
     */
    public List<Brand> queryBrandList(Long cid) {
        return this.brandMapper.queryBrandList(cid);
    }


    /**
     * 根据品牌id集合查询品牌
     * @param brandIds 品牌id集合
     * @return 品牌
     */
    public List<Brand> queryListByIds(List<Long> brandIds) {
        return this.brandMapper.selectBatchIds(brandIds);
    }
}
