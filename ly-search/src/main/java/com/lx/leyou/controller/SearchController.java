package com.lx.leyou.controller;

import com.lx.leyou.entity.Goods;
import com.lx.leyou.entity.PageResult;
import com.lx.leyou.entity.SearchRequest;
import com.lx.leyou.repository.GoodsRepository;
import com.lx.leyou.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SearchController {

    @Autowired
    public SearchService searchService;

    @Autowired
    public GoodsRepository goodsRepository;

    @PostMapping("/page")
    public ResponseEntity<PageResult<Goods>> searchGoods(@RequestBody SearchRequest request) {
        PageResult<Goods> goods = this.searchService.search(request);
        if (goods == null || CollectionUtils.isEmpty(goods.getItems())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(goods);
    }
}
