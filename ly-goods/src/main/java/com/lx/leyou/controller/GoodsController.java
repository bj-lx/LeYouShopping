package com.lx.leyou.controller;

import com.lx.leyou.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/item")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /**
     * 根据spuId创建商品数据模型
     * @param model 数据模型
     * @param spuId spuId
     * @return 商品数据模型
     */
    @GetMapping("/{spuId}.html")
    public String toGoods (Model model, @PathVariable("spuId") Long spuId) {
        Map<String, Object> map = this.goodsService.loadModel(spuId);
        model.addAllAttributes(map);
        return "item";
    }
}
