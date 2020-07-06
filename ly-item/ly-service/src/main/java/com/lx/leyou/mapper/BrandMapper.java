package com.lx.leyou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lx.entity.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BrandMapper extends BaseMapper<Brand> {
    @Insert("insert into tb_category_brand(category_id, brand_id) values(#{cid}, #{bid})")
    void insertBrandAndCategory(@Param("cid") Long id, @Param("bid") Long cids);

    @Select("SELECT\n" +
            "\t* \n" +
            "FROM\n" +
            "\ttb_brand\n" +
            "\tJOIN tb_category_brand ON tb_brand.id = tb_category_brand.brand_id \n" +
            "WHERE\n" +
            "\ttb_category_brand.category_id = #{cid}")
    List<Brand> queryBrandList(Long cid);
}
