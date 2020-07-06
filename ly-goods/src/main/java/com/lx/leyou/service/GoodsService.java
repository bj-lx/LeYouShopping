package com.lx.leyou.service;

import com.lx.entity.*;
import com.lx.leyou.client.BrandClient;
import com.lx.leyou.client.CategoryClient;
import com.lx.leyou.client.GoodsClient;
import com.lx.leyou.client.SpecGroupParamClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GoodsService {
    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecGroupParamClient specGroupParamClient;

    /**
     * 根据spuId创建商品数据模型
     * @param spuId spuId
     * @return 商品数据模型
     */
    public Map<String, Object> loadModel(Long spuId) {
        Map<String, Object> model = new HashMap<>();

        //查询spu
        Spu spu = this.goodsClient.querySpuById(spuId);
        //查询spuDetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailById(spuId);
        //查询skus
        List<Sku> skus = this.goodsClient.querySkuListById(spuId);
        //查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());
        //查询分类
        List<Long> ids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> names = this.categoryClient.queryNameByIds(ids);
        List<Map<String, Object>> categories = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", ids.get(i));
            map.put("name", names.get(i));
            categories.add(map);
        }

        //查询规格组及参数
        List<SpecGroup> groups = this.specGroupParamClient.querySpecGroupAndSpecParam(spu.getCid3());
        //处理规格参数
        List<SpecParam> specParams = this.specGroupParamClient.querySpecParamList(null, spu.getCid3(), false, null);
        Map<Long, Object> paramMap = new HashMap<>();
        specParams.forEach(specParam -> {
            paramMap.put(specParam.getId(), specParam.getName());
        });

        model.put("spu", spu);
        model.put("spuDetail", spuDetail);
        model.put("skus", skus);
        model.put("brand", brand);
        model.put("categories", categories);
        model.put("groups", groups);
        model.put("paramMap", paramMap);
        return model;
    }
}
