package com.lx.bo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.lx.entity.Sku;
import com.lx.entity.Spu;
import com.lx.entity.SpuDetail;

import java.util.List;

public class SpuBo extends Spu {

    @TableField(exist = false)
    String cname;// 商品分类名称
    @TableField(exist = false)
    String bname;// 品牌名称
    @TableField(exist = false)
    SpuDetail spuDetail;// 商品详情
    @TableField(exist = false)
    List<Sku> skus;// sku列表

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }

    public SpuDetail getSpuDetail() {
        return spuDetail;
    }

    public void setSpuDetail(SpuDetail spuDetail) {
        this.spuDetail = spuDetail;
    }

    public List<Sku> getSkus() {
        return skus;
    }

    public void setSkus(List<Sku> skus) {
        this.skus = skus;
    }
}
