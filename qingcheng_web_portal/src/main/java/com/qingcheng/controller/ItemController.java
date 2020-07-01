package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.qingcheng.pojo.goods.Goods;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.goods.Spu;
import com.qingcheng.service.goods.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 戴金华
 * @date 2019-10-28 17:49
 */
@RestController
@RequestMapping("/item")
public class ItemController {

    @Reference
    private SpuService spuService;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${pagePath}")
    private String pagePath;

    @GetMapping("/createPage")
     public void createPage(String id){
        //获取spu和sku的集合
        Goods goods = spuService.findGoodsById(id);
        //获取spu信息
        Spu spu = goods.getSpu();
        //获取sku信息
        List<Sku> skuList = goods.getSkuList();

        //生成url 的map
        Map urlMap = new HashMap();
        for (Sku sku : skuList) {
            String key = JSON.toJSONString(JSON.parseObject(sku.getSpec()), SerializerFeature.MapSortField);
            urlMap.put(key,sku.getId()+".html");
        }

        //每个sku生成一个页面
        for (Sku sku : skuList) {
            //上下文
            Context context = new Context();
            //创建数据模型
            HashMap<String, Object> dataModel = new HashMap<>();
            dataModel.put("spu",spu);
            dataModel.put("sku",sku);

            String image = spu.getImage();
            //sku和spu图片
            dataModel.put("spuImages",spu.getImages().split(","));
            dataModel.put("skuImages",sku.getImages().split(","));


            //sku和spu的参数列表
            Map paramItems = JSON.parseObject(spu.getParaItems());
            dataModel.put("paramItems",paramItems);

            Map specItems = JSON.parseObject(sku.getSpec());
            dataModel.put("specItems",specItems);


            /**
             * {"颜色":[{"option":"红","checked":true},{"option":"黑","checked":false},{"option":"绿","checked":false}],
             * "机身内存":[{"option":"4G","checked":""}]
             *
             *
             */
            Map<String,List> specMap = (Map<String, List>) JSON.parse(spu.getSpecItems());
            for (String key : specMap.keySet()) {
                List<Map> mapList = new ArrayList();
                List<String> list = specMap.get(key);
                for (String value : list) {
                    HashMap hashMap = new HashMap();
                    hashMap.put("option",value);
                    if (value.equals(specItems.get(key))){
                        //如果与sku中的spec一样
                        hashMap.put("checked",true);
                    }else{
                        hashMap.put("checked",false);
                    }
                    //商品详细地址 {"颜色":"黑","机身内存":"64G"}
                    Map spec = JSON.parseObject(sku.getSpec()); //当前sku规格
                    spec.put(key,value);
                    String specJson = JSON.toJSONString(spec, SerializerFeature.MapSortField);
                    hashMap.put("url",(String)urlMap.get(specJson));
                    mapList.add(hashMap);
                }
                specMap.put(key,mapList);
            }
            dataModel.put("specMap",specMap);
            context.setVariables(dataModel);



            //准备文件
            File file = new File(pagePath);
            if (!file.exists()){
                file.mkdirs();
            }

            File dest = new File(file, sku.getId() + ".html");
            //生成页面
            try {
                PrintWriter writer = new PrintWriter(dest, "utf-8");
                templateEngine.process("item",context,writer);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
