package com.lx.leyou.service;

import com.lx.bo.SpuBo;
import com.lx.leyou.client.GoodsClient;
import com.lx.leyou.entity.Goods;
import com.lx.leyou.entity.PageResult;
import com.lx.leyou.repository.GoodsRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
@MapperScan("com.lx.leyou.service")
public class SearchServiceTest {

    @Autowired
    public ElasticsearchTemplate template;

    @Autowired
    public GoodsClient goodsClient;

    @Autowired
    public SearchService searchService;

    @Autowired
    public GoodsRepository goodsRepository;

    @Test
    public void loadData(){
        // 创建索引
        this.template.createIndex(Goods.class);
        // 配置映射
        this.template.putMapping(Goods.class);
        int page = 1;
        int rows = 100;
        int size = 0;
        do {
            // 查询分页数据
            PageResult<SpuBo> result = this.goodsClient.queryGoods( null, null, page, rows);
            List<SpuBo> spuBos = result.getItems();
            size = spuBos.size();
            // 遍历spu
            List<Goods> goods = spuBos.stream().map(spuBo -> {
                try {
                    return this.searchService.buildGoods(spuBo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
            this.goodsRepository.saveAll(goods);
            page++;
        } while (size == 100);
    }



    @Test
    public void test3() {
        System.out.println(123);
    }
}
