package com.lx.leyou.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lx.entity.SpecGroup;
import com.lx.entity.SpecParam;
import com.lx.leyou.mapper.SpecGroupMapper;
import com.lx.leyou.mapper.SpecParamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecGroupParamService {

    @Autowired
    public SpecGroupMapper specGroupMapper;

    @Autowired
    public SpecParamMapper specParamMapper;


    /**
     *根据分类id查询规格
     * @param cid 分类id
     * @return 规格
     */
    public List<SpecGroup> querySpecGroup(Long cid) {
        QueryWrapper<SpecGroup> qw = new QueryWrapper<>();
        qw.eq("cid", cid);
        return this.specGroupMapper.selectList(qw);
    }

    /**
     * 根据条件查询规格参数
     * @param gid 品牌id
     * @param cid 分类id
     * @param generic 是否是通用规格参数
     * @param searching 是否为搜索关键字
     * @return 规格参数
     */
    public List<SpecParam> querySpecParamList(Long gid, Long cid, Boolean generic, Boolean searching) {
        QueryWrapper<SpecParam> qw = new QueryWrapper<>();
        qw.eq(gid != null, "group_id", gid);
        qw.eq(cid != null, "cid", cid);
        qw.eq(generic != null, "generic", generic);
        qw.eq(searching != null, "searching", searching);
        return this.specParamMapper.selectList(qw);
    }

    /**
     * 根据分类id查询规格组和组内参数
     * @param cid 分类id
     * @return 规格组和组内参数
     */
    public List<SpecGroup> querySpecGroupAndSpecParam(Long cid) {
        List<SpecGroup> groups = this.querySpecGroup(cid);

        groups.forEach(group -> {
            group.setParams(this.querySpecParamList(group.getId(), null, null, null));
        });

        return groups;
    }
}
