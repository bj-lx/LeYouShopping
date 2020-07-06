package com.lx.leyou.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.bo.SpuBo;
import com.lx.entity.*;
import com.lx.leyou.entity.PageResult;
import com.lx.leyou.mapper.SkuMapper;
import com.lx.leyou.mapper.SpuDetailMapper;
import com.lx.leyou.mapper.SpuMapper;
import com.lx.leyou.mapper.StockMapper;
import com.sun.deploy.util.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 根据搜索条件和上下架情况分页查询商品列表
     * @param key 搜索条件
     * @param saleable 上下架情况
     * @param page 当前页码
     * @param rows 每页记录数
     * @return 商品列表
     */
    public PageResult<SpuBo> queryGoods(String key, Boolean saleable, Integer page, Integer rows) {

        //添加条件
        QueryWrapper<Spu> qw = new QueryWrapper<>();
        if (key != null) {
            qw.like("title", key);
        }

        if (saleable != null) {
            qw.eq("saleable", saleable);
        }

        IPage<Spu> spuPage = new Page<>();
        spuPage.setCurrent(page);
        spuPage.setSize(rows);

        //查询结果
        IPage<Spu> spuIPage = this.spuMapper.selectPage(spuPage, qw);
        List<Spu> result = spuIPage.getRecords();

        //将结果转你换为SpuBo
        List<SpuBo> collect = result.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            BeanUtils.copyProperties(spu, spuBo);

            //查询品牌名称
            Brand brand = this.brandService.queryBrandById(spu.getBrandId());
            spuBo.setBname(brand.getName());

            //查询分类名称
            List<Long> ids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
            List<String> categories = this.categoryService.queryCategoryNameList(ids);
            spuBo.setCname(StringUtils.join(categories, "-"));

            return spuBo;
        }).collect(Collectors.toList());
        //返回SpuBo
        PageResult<SpuBo> pageResult = new PageResult<>();
        pageResult.setTotal(spuIPage.getPages());
        pageResult.setTotalPage((int) spuIPage.getSize());
        pageResult.setItems(collect);

        return pageResult;
    }

    /**
     * 新增商品
     * @param spuBo 商品信息
     */
    @Transactional
    public void saveGood(SpuBo spuBo) {
        //新增spu、spuDetail
        saveAndUpdateBrand(spuBo);

        sendMessage(spuBo.getId(), "insert");
    }

    private void sendMessage(Long id, String type) {
        try {
            this.amqpTemplate.convertAndSend("item." + type, id);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }

    private void saveAndUpdateBrand(SpuBo spuBo) {
        //新增or更新spu
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        spuBo.insertOrUpdate();

        //新增or更新spuDetail
        SpuDetail spuDetail = spuBo.getSpuDetail();
        System.out.println(spuBo.getId());
        spuDetail.setSpuId(spuBo.getId());
        spuDetail.insertOrUpdate();

        //新增sku
        List<Sku> skus = spuBo.getSkus();
        skus.forEach(sku -> {
            if (!sku.getEnable()) {
                return;
            }
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            sku.setSpuId(spuBo.getId());
            sku.insert();

            //新增sku库存
            Stock stock = new Stock();
            System.out.println(sku.getId());
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stock.insert();
        });
    }


    /**
     * 根据spuId查询spuDetail
     * @param spuId spuId
     * @return spuDetail
     */
    public SpuDetail querySpuDetailById(Long spuId) {
        //查询spuDetail
        return this.spuDetailMapper.selectById(spuId);
    }

    /**
     * 根据spuId查询sku集合
     * @param id spuId
     * @return sku集合
     */
    public List<Sku> querySkuListById(Long id) {
        //查询spu
        Spu spu = this.spuMapper.selectById(id);
        //查询sku
        QueryWrapper<Sku> qw = new QueryWrapper<>();
        qw.eq("spu_id", spu.getId());
        List<Sku> skus = this.skuMapper.selectList(qw);

        skus.forEach(sku -> {
            //查询库存
            Stock stock = this.stockMapper.selectById(sku.getId());
            sku.setStock(stock.getStock());
        });

        return skus;
    }

    /**
     * 修改商品
     * @param spuBo 商品信息
     */
    @Transactional
    public void updateGood(SpuBo spuBo) {
        //查询skqId
        QueryWrapper<Sku> qw = new QueryWrapper<>();
        qw.eq("spu_id", spuBo.getId());
        List<Sku> skus = this.skuMapper.selectList(qw);
        skus.forEach(sku -> {
            //删除stock
            this.stockMapper.deleteById(sku.getId());
            //删除sku
            this.skuMapper.deleteById(sku.getId());
        });

        //更新spu、spuDetail
        //新增stock、sku
        saveAndUpdateBrand(spuBo);

        sendMessage(spuBo.getId(), "update");
    }

    /**
     * 根据spuId查询spu
     * @param spuId spuId
     * @return spu
     */
    public Spu querySpuById(Long spuId) {
        return this.spuMapper.selectById(spuId);
    }

    /**
     * 根据skuId查询sku
     * @param skuId skuId
     * @return sku
     */
    public Sku querySkuBySkuId(Long skuId) {
        return this.skuMapper.selectById(skuId);
    }


}

