package com.lx.api;

import com.lx.entity.SpecGroup;
import com.lx.entity.SpecParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/spec")
public interface SpecGroupParamApi {

    /**
     *根据分类id查询规格
     * @param cid 分类id
     * @return 规格
     */
    @GetMapping("/groups/{cid}")
    List<SpecGroup> querySpecGroup(@PathVariable("cid") Long cid);

    /**
     * 根据条件查询规格参数
     * @param gid 品牌id
     * @param cid 分类id
     * @param generic 是否是通用规格参数
     * @param searching 是否为搜索关键字
     * @return 规格参数
     */
    @GetMapping("/params")
    List<SpecParam> querySpecParamList(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "generic", required = false) Boolean generic,
            @RequestParam(value = "searching", required = false) Boolean searching);

    /**
     * 根据分类id查询规格组和组内参数
     * @param cid 分类id
     * @return 规格组和组内参数
     */
    @GetMapping("{cid}")
    List<SpecGroup> querySpecGroupAndSpecParam(@PathVariable("cid") Long cid);
}
