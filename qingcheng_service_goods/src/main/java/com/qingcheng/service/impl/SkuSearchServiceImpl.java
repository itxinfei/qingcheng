package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.BrandMapper;
import com.qingcheng.dao.SkuMapper;
import com.qingcheng.dao.SpecMapper;
import com.qingcheng.service.goods.BrandService;
import com.qingcheng.service.goods.SkuSearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 戴金华
 * @date 2019-11-25 18:30
 */
@Service(interfaceClass = SkuSearchService.class)
public class SkuSearchServiceImpl implements SkuSearchService {


    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private SpecMapper specMapper;

    @Override
    public Map search(Map<String, String> searchMap) {

        //1.分装请求查询
        SearchRequest searchRequest = new SearchRequest("sku");
        searchRequest.types("doc");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //布尔查询构建器
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //1.1关键字搜索
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", searchMap.get("keywords"));
        boolQueryBuilder.must(matchQueryBuilder);
        //1.2聚合查询（商品分类）
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("sku_category").field("categoryName");
        searchSourceBuilder.aggregation(termsAggregationBuilder);

        //1.2.1商品分类过滤
        if (searchMap.get("category")!=null){
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("categoryName", searchMap.get("category"));
            boolQueryBuilder.filter(termQueryBuilder);
        }

        //1.3品牌过滤查询
        if(searchMap.get("brand")!=null){
            System.out.println(searchMap.get("brand"));
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("brandName", searchMap.get("brand"));
            boolQueryBuilder.filter(termQueryBuilder);
        }

        // 1.4规格过滤查询
        for (String key : searchMap.keySet()) {
            if (key.startsWith("spec.")){
                TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(key + ".keyword", searchMap.get(key));
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }


        //1.5价格过滤
        if (searchMap.get("price")!=null){
            String[] price = searchMap.get("price").split("-");
            if (!price[0].equals("0")){     //最低价格不等于0
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price").gte(price[0] + "00");
                boolQueryBuilder.filter(rangeQueryBuilder);
            }

            if (!price[1].equals("*")){     //最高价格上线不为*
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price").lte(price[1] + "00");
                boolQueryBuilder.filter(rangeQueryBuilder);
            }
        }

        //1.6分页
        Integer pageNo = Integer.parseInt(searchMap.get("pageNo")); //页码
        Integer pageSize = 30;  //页大小
        int fromIndex = (pageNo-1)*pageSize;    //起始记录下标
        searchSourceBuilder.from(fromIndex);
        searchSourceBuilder.size(pageSize);

        //1.7 排序
        String sort = searchMap.get("sort");    //排序字段
        String sortOrder = searchMap.get("sortOrder");  //排序规则
        if (!"".equals(sort)){
            searchSourceBuilder.sort(sort, SortOrder.valueOf(sortOrder));
        }

        //1.8 高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name").preTags("<font style='color:red'>").postTags("</font");

        searchSourceBuilder.highlighter(highlightBuilder);
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);

        //2.分装查询结果
        Map resultMap = new HashMap();
        SearchResponse searchResponse = null;
        try {
           searchResponse  = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            long totalHits = searchHits.getTotalHits();
            System.out.println("查询结果的总记录数是:"+totalHits);
            SearchHit[] hits = searchHits.getHits();


            //2.1商品列表
            ArrayList<Map<String, Object>> resultList = new ArrayList<>();
            for (SearchHit hit : hits) {
                Map<String, Object> skuMap = hit.getSourceAsMap();

                //name高亮处理
                Map<String, HighlightField> highlightField = hit.getHighlightFields();
                HighlightField name = highlightField.get("name");
                Text[] fragments = name.fragments();
                skuMap.put("name",fragments[0].toString()); //用高亮的内容替换原内容
                resultList.add(skuMap);
            }
            //2.2 商品分类列表
            Aggregations aggregations = searchResponse.getAggregations();
            Map<String, Aggregation> aggregationMap = aggregations.getAsMap();
            Terms terms = (Terms) aggregationMap.get("sku_category");

            List<? extends Terms.Bucket> buckets = terms.getBuckets();
            ArrayList<String> categoryList = new ArrayList<>();
            for (Terms.Bucket bucket : buckets) {
                categoryList.add(bucket.getKeyAsString());
            }

            //2.3品牌列表
            String categoryName = "";
            if (searchMap.get("category")==null){
                //如果没有分类条件
                if (categoryList.size()>0){
                    //提取分类列表的第一个页面
                    categoryName = categoryList.get(0);
                }
            }else{
                //如果有分类条件
                categoryName = searchMap.get("category");
            }
            List<Map> brandList = brandMapper.findListByCategoryName(categoryName);



            //2.4 规格列表
            List<Map> specList = specMapper.findListByCategoryName(categoryName);
            System.out.println(specList);
            for (Map spec : specList) {
                String[] options = ((String) spec.get("options")).split(",");
                spec.put("options",options);
            }

            //2.5总页数
            long totalCount = searchHits.getTotalHits();
            long pageCount = (totalCount%pageSize==0)?(totalCount/pageSize):(totalCount/pageSize+1);
            resultMap.put("categoryList",categoryList);
            resultMap.put("rows",resultList);
            resultMap.put("brandList",brandList);   //品牌列表
            resultMap.put("specList",specList);     //规格选项列表
            resultMap.put("totalPages",pageCount);  //分页的总页数
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultMap;
    }
}
