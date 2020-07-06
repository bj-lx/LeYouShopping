package com.lx.leyou.controller;

import com.lx.entity.SpecGroup;
import com.lx.entity.SpecParam;
import com.lx.leyou.service.SpecGroupParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/spec")
public class SpecGroupParamController {

    @Autowired
    public SpecGroupParamService specGroupParamService;

    /**
     *根据分类id查询规格
     * @param cid 分类id
     * @return 规格
     */
    @GetMapping("/groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroup(@PathVariable("cid") Long cid) {
        List<SpecGroup> specGroups = this.specGroupParamService.querySpecGroup(cid);
        if (CollectionUtils.isEmpty(specGroups)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(specGroups);
    }

    /**
     * 根据条件查询规格参数
     * @param gid 品牌id
     * @param cid 分类id
     * @param generic 是否是通用规格参数
     * @param searching 是否为搜索关键字
     * @return 规格参数
     */
    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> querySpecParamList(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "generic", required = false) Boolean generic,
            @RequestParam(value = "searching", required = false) Boolean searching) {
        List<SpecParam> specParams = this.specGroupParamService.querySpecParamList(gid, cid, generic, searching);
        if (CollectionUtils.isEmpty(specParams)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(specParams);
    }

    /**
     * 根据分类id查询规格组和组内参数
     * @param cid 分类id
     * @return 规格组和组内参数
     */
    @GetMapping("{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroupAndSpecParam(
            @PathVariable("cid") Long cid) {
        List<SpecGroup> specGroups = this.specGroupParamService.querySpecGroupAndSpecParam(cid);
        if (CollectionUtils.isEmpty(specGroups)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(specGroups);
    }
}
