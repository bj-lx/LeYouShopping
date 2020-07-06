package com.lx.leyou.entity;

import com.lx.entity.Brand;

import java.util.List;
import java.util.Map;

public class SearchPage<T> extends PageResult<T> {

    private List<Brand> brands;
    private List<Map<String, Object>> categories;
    private List<Map<String, Object>> specParams;

    public SearchPage() {
    }

    public SearchPage(Long total, Integer totalPage, List<T> items, List<Brand> brands, List<Map<String, Object>> categories, List<Map<String, Object>> specParams) {
        super(total, totalPage, items);
        this.brands = brands;
        this.categories = categories;
        this.specParams = specParams;
    }


    public List<Brand> getBrands() {
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }

    public List<Map<String, Object>> getCategories() {
        return categories;
    }

    public void setCategories(List<Map<String, Object>> categories) {
        this.categories = categories;
    }

    public List<Map<String, Object>> getSpecParams() {
        return specParams;
    }

    public void setSpecParams(List<Map<String, Object>> specParams) {
        this.specParams = specParams;
    }
}
