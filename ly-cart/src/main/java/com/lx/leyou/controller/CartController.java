package com.lx.leyou.controller;

import com.lx.leyou.entity.Cart;
import com.lx.leyou.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 新增购物车
     * @param cart 购物车
     * @return 无
     */
    @PostMapping
    public ResponseEntity<Void> saveInCart(@RequestBody Cart cart) {
        this.cartService.saveInCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 查询用户购物车
     * @return 购物车
     */
    @GetMapping
    public ResponseEntity<List<Cart>> queryListCart() {
        List<Cart> carts = this.cartService.queryListCart();
        if (CollectionUtils.isEmpty(carts)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(carts);
    }

    /**
     * 修改用户购物车
     * @return 购物车
     */
    @PutMapping
    public ResponseEntity<Void> updateCart(@RequestBody Cart cart) {
        this.cartService.updateCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据skuId删除购物车商品
     * @param skuId skuId
     * @return 无
     */
    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId") String skuId) {
        this.cartService.deleteCart(skuId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
