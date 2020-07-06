package com.lx.leyou.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lx.entity.*;
import com.lx.leyou.client.BrandClient;
import com.lx.leyou.client.CategoryClient;
import com.lx.leyou.client.GoodsClient;
import com.lx.leyou.client.SpecGroupParamClient;
import com.lx.leyou.entity.Goods;
import com.lx.leyou.entity.SearchPage;
import com.lx.leyou.entity.SearchRequest;
import com.lx.leyou.repository.GoodsRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    public BrandClient brandClient;

    @Autowired
    public CategoryClient categoryClient;

    @Autowired
    public SpecGroupParamClient specGroupParamClient;

    @Autowired
    public GoodsClient goodsClient;

    @Autowired
    public GoodsRepository goodsRepository;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Goods buildGoods(Spu spu) throws IOException {

        //查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());
        //查询分类名称
        List<String> names = this.categoryClient.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        //查询sku的价格
        List<Sku> skus = this.goodsClient.querySkuListById(spu.getId());
        //创建价格集合
        List<Long> price = new ArrayList<>();
        //创建需要用到的sku字段
        List<Map<String, Object>> skuBo = new ArrayList<>();
        skus.forEach(sku -> {
            price.add(sku.getPrice());
            Map<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("title", sku.getTitle());
            map.put("price", sku.getPrice());
            //如果图片是多张取第一张
            map.put("images", StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            map.put("ownSpec", sku.getOwnSpec());
            skuBo.add(map);
        });

        //查询所有的规格规格参数
        List<SpecParam> specParams = this.specGroupParamClient.querySpecParamList(null, spu.getCid3(), null, true);
        //根据spuId查询SpuDetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailById(spu.getId());

        //反序列化通用的规格参数
        Map<String, Object> genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<String, Object>>(){});
        //反序列化非通用的规格参数
        Map<String, List<Object>> specialSpecMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<String, List<Object>>>(){});

        Map<String, Object> specs = new HashMap<>();
        specParams.forEach(param -> {
            if (param.getGeneric()) {
                //是通用的规格参数
                String value = genericSpecMap.get(param.getId().toString()).toString();
                //判断搜索字段是否是数字
                if (param.getNumeric()) {
                    value = chooseSegment(value, param);
                }

                specs.put(param.getName(), value);
            } else {
                //不是通用的规格参数
                List<Object> value = specialSpecMap.get(param.getId().toString());
                specs.put(param.getName(), value);
            }
        });

        Goods goods = new Goods();
        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setAll(spu.getTitle() + " " + StringUtils.join(names, " ") + " " + brand.getName());
        goods.setSubTitle(spu.getSubTitle());
        goods.setPrice(price);
        goods.setSkus(MAPPER.writeValueAsString(skuBo));
        goods.setSpecs(specs);

        return goods;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    public SearchPage<Goods> search(SearchRequest request) {
        if (request == null) {
            return null;
        }
        //创建自定义查询构造器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加查询条件
        //MatchQueryBuilder builder = QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND);
        BoolQueryBuilder builder = getBoolQueryBuilder(request);
        queryBuilder.withQuery(builder);
        //过滤结果集
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "skus", "subTitle"}, null));
        //分页
        queryBuilder.withPageable(PageRequest.of(request.getPage() - 1, request.getSize()));
        //排序
        String sort = request.getSort();
        boolean desc = request.getDesc();
        if (!StringUtils.isBlank(sort)) {
            queryBuilder.withSort(SortBuilders.fieldSort(sort).order(desc ? SortOrder.DESC : SortOrder.ASC));
        }
        //聚合品牌
        queryBuilder.addAggregation(AggregationBuilders.terms("brands").field("brandId"));
        //聚合分类
        queryBuilder.addAggregation(AggregationBuilders.terms("categories").field("cid3"));
        //执行查询
        AggregatedPage<Goods> search = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());
        //解析品牌聚合结果集
        List<Brand> brands = getBrandAggResult(search.getAggregation("brands"));
        //解析分类聚合结果集
        List<Map<String, Object>> categories = getCategoryAggResult(search.getAggregation("categories"));

        List<Map<String, Object>> specs = null;
        //判断分类是否为一个
        if (categories.size() == 1) {
            specs = getSpecParamAggResult(categories.get(0).get("id"), builder);
        }

        long totalPage = search.getTotalElements()%20 == 0 ? search.getTotalElements()/20 : search.getTotalElements()/20 + 1;

        return new SearchPage<>(search.getTotalElements(), (int) totalPage, search.getContent(), brands, categories, specs);
    }

    private BoolQueryBuilder getBoolQueryBuilder(SearchRequest request) {
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        //基本查询
        queryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND));
        //过滤条件
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 整理过滤条件
        Map<String, Object> filter = request.getFilter();
        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            String key = entry.getKey();
            String value = (String) entry.getValue();

            if (key.equals("品牌")) {
                key = "brandId";
            } else if (key.equals("分类")) {
                key = "cid3";
            } else {
                key = "specs." + key + ".keyword";
            }
            // 字符串类型，进行term查询
            boolQuery.must(QueryBuilders.termQuery(key, value));
        }
        // 添加过滤条件
        queryBuilder.filter(boolQuery);
        return queryBuilder;
    }

    private List<Map<String, Object>> getSpecParamAggResult(Object id, QueryBuilder builder) {
        //创建自定义查询构造器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加查询条件
        queryBuilder.withQuery(builder);

        //获取规格参数
        List<SpecParam> params = this.specGroupParamClient.querySpecParamList(null, (Long) id, null, true);
        //聚合规格
        params.forEach(param -> {
            queryBuilder.addAggregation(AggregationBuilders.terms(param.getName()).field("specs." + param.getName() + ".keyword"));
        });
        //过滤结果集
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));
        //执行查询
        AggregatedPage<Goods> search = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());

        List<Map<String, Object>> specs = new ArrayList<>();
        //解析规格参数聚合结果集
        Map<String, Aggregation> asMap = search.getAggregations().asMap();
        for (Map.Entry<String, Aggregation> entry : asMap.entrySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("key", entry.getKey());
            //获取聚合
            StringTerms terms = (StringTerms) entry.getValue();
            //获取聚合中的桶
            List<String> option = terms.getBuckets().stream().map(StringTerms.Bucket::getKeyAsString).collect(Collectors.toList());
            map.put("options", option);
            specs.add(map);
        }
        return specs;
    }

    private List<Map<String, Object>> getCategoryAggResult(Aggregation aggregation) {
        //解析聚合结果集
        LongTerms terms = (LongTerms) aggregation;

        return terms.getBuckets().stream().map(bucket -> {
            Map<String, Object> map = new HashMap<>();
            long id = bucket.getKeyAsNumber().longValue();
            List<String> names = this.categoryClient.queryNameByIds(Collections.singletonList(id));
            map.put("id", id);
            map.put("name", names.get(0));
            return map;
        }).collect(Collectors.toList());
    }

    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        //解析聚合结果集
        LongTerms terms = (LongTerms) aggregation;

        return terms.getBuckets().stream().map(bucket -> {
            //根据品牌id查询品牌
            return this.brandClient.queryBrandById(bucket.getKeyAsNumber().longValue());
        }).collect(Collectors.toList());

    }

    public void  save(Long id) throws IOException {

        //查询spu
        Spu spu = this.goodsClient.querySpuById(id);
        //构建商品
        Goods goods = this.buildGoods(spu);
        //保存或者覆盖商品
        this.goodsRepository.save(goods);
    }
}
