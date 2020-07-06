package com.lx.leyou.service;

import com.lx.entity.Sku;
import com.lx.leyou.client.GoodsClient;
import com.lx.leyou.entity.Cart;
import com.lx.leyou.interceptor.LoginInterceptor;
import com.lx.leyou.pojo.UserInfo;
import com.lx.leyou.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GoodsClient goodsClient;

    private static final String KEY_PREFIX = "LeYou:cart:userId";

    /**
     * 新增购物车
     *
     * @param cart 购物车
     */
    public void saveInCart(Cart cart) {
        //获取redis操作对象
        BoundHashOperations<String, Object, Object> hashOps = getHashOps();
        if (hashOps == null) {
            return ;
        }
        //获取购物车信息
        Long skuId = cart.getSkuId();
        Integer num = cart.getNum();
        Boolean boo = hashOps.hasKey(skuId.toString());
        //判断购物车是否存在
        if (boo) {
            //存在
            //获取购物车返回json字符串
            String json = Objects.requireNonNull(hashOps.get(skuId.toString())).toString();
            //反序列化为cart
            cart = JsonUtils.parse(json, Cart.class);
            if (cart != null) {
                cart.setNum(cart.getNum() + num);
            }
        } else {
            //不存在
            //根据skuId查询sku
            Sku sku = this.goodsClient.querySkuBySkuId(skuId);
            //初始化cart
            cart.setUserId(LoginInterceptor.get().getId());
            cart.setImage(StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            cart.setPrice(sku.getPrice());
            cart.setTitle(sku.getTitle());
            cart.setOwnSpec(sku.getOwnSpec());
        }
        //将购物车数据写入redis
        if (cart != null) {
            hashOps.put(cart.getSkuId().toString(), Objects.requireNonNull(JsonUtils.serialize(cart)));
        }
    }

    /**
     * 查询用户购物车
     * @return 购物车
     */
    public List<Cart> queryListCart() {
        //获取redis操作对象
        BoundHashOperations<String, Object, Object> hashOps = getHashOps();
        if (hashOps == null) return null;

        //获取购物车内容
        List<Object> cartJsons = hashOps.values();
        if (CollectionUtils.isEmpty(cartJsons)) {
            return null;
        }

        return cartJsons.stream().map(cartJson -> {
            return JsonUtils.parse(cartJson.toString(), Cart.class);
        }).collect(Collectors.toList());
    }

    /**
     * 获取redis操作对象（购物车）
     * @return HashOperations
     */
    private BoundHashOperations<String, Object, Object> getHashOps() {
        //获取用户
        UserInfo userInfo = LoginInterceptor.get();
        if (userInfo == null) {
            return null;
        }
        //hash的key
        String key = KEY_PREFIX + userInfo.getId();
        //获取购物车
        return this.redisTemplate.boundHashOps(key);
    }

    /**
     * 修改用户购物车
     */
    public void updateCart(Cart cart) {
        //获取redis操作对象
        BoundHashOperations<String, Object, Object> hashOps = getHashOps();
        if (hashOps == null) {
            return ;
        }
        Long skuId = cart.getSkuId();
        Integer num = cart.getNum();
        //获取购物车返回json字符串
        String json = Objects.requireNonNull(hashOps.get(skuId.toString())).toString();
        //反序列化为cart
        cart = JsonUtils.parse(json, Cart.class);
        if (cart != null) {
            cart.setNum(num);
        }
        //将购物车数据写入redis
        if (cart != null) {
            hashOps.put(cart.getSkuId().toString(), Objects.requireNonNull(JsonUtils.serialize(cart)));
        }
    }

    /**
     * 根据skuId删除购物车商品
     * @param skuId skuId
     */
    public void deleteCart(String skuId) {
        //获取redis操作对象
        BoundHashOperations<String, Object, Object> hashOps = getHashOps();
        if (hashOps == null) {
            return ;
        }
        hashOps.delete(skuId);
    }
}
