package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.*;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.*;
import com.qingcheng.service.goods.SpuService;
import com.qingcheng.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SpuService.class)
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private CategoryBrandMapper categoryBrandMapper;
    @Autowired
    private AuditLogMapper auditLogMapper;

    /**
     * 返回全部记录
     *
     * @return
     */
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Spu> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        Page<Spu> spus = (Page<Spu>) spuMapper.selectAll();
        return new PageResult<Spu>(spus.getTotal(), spus.getResult());
    }

    /**
     * 条件查询
     *
     * @param searchMap 查询条件
     * @return
     */
    public List<Spu> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return spuMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     *
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Spu> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        Page<Spu> spus = (Page<Spu>) spuMapper.selectByExample(example);
        return new PageResult<Spu>(spus.getTotal(), spus.getResult());
    }

    /**
     * 根据Id查询
     *
     * @param id
     * @return
     */
    public Spu findById(String id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     *
     * @param spu
     */
    public void add(Spu spu) {
        spuMapper.insert(spu);
    }

    /**
     * 修改
     *
     * @param spu
     */
    public void update(Spu spu) {
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void delete(String id) {
        spuMapper.deleteByPrimaryKey(id);
    }

    /**
     * 保存商品
     *
     * @param goods
     */
    @Transactional
    @Override
    public void saveGoods(Goods goods) {
        //保存一个spu信息
        Spu spu = goods.getSpu();

        String id = spu.getId();
        //判断spu的id是否为空
        if (id == null || id == "") {
            //如果id为空 则表明是添加操作
            //分布式id
            spu.setId(idWorker.nextId() + "");
            spuMapper.insert(spu);
        }else{
            //如果id不为空 则表明是修改操作
            //删除原有的sku列表
            Example example = new Example(Sku.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("spuId",id);
            skuMapper.deleteByExample(example);
            //更新spu
            spuMapper.updateByPrimaryKeySelective(spu);
        }


        CategoryBrand categoryBrand = new CategoryBrand();
        categoryBrand.setBrandId(spu.getBrandId());
        categoryBrand.setCategoryId(spu.getCategory3Id());
        int count = categoryBrandMapper.selectCountByExample(categoryBrand);
        if (count==0){
            //如果表中没有数据,则将数据添加到tb_category_brand中
            categoryBrandMapper.insert(categoryBrand);
        }

        Date date = new Date();
        //根据分类id 获得分类对象
        Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());
        //保存sku信息
        List<Sku> skuList = goods.getSkuList();
        for (Sku sku : skuList) {
            //设置sku的分布式id
            sku.setId(idWorker.nextId() + "");
            //sku名称 sku = spu名称+规格值列表
            String name = spu.getName();
            //sku.getSpec() {"颜色":"红","机身内存":"64G"} 转换成Map
            Map<String, String> map = JSON.parseObject(sku.getSpec(), Map.class);
            for (String value : map.values()) {
                name += " " + value;
            }
            //设置sku的名称
            sku.setName(name);
            //设置sku的spu id
            sku.setSpuId(spu.getId());
            sku.setCreateTime(date);    //创建日期
            sku.setUpdateTime(date);    //修改日期
            sku.setCategoryId(spu.getCategory3Id());    //分类id
            sku.setCategoryName(category.getName());    //分类名称
            sku.setCommentNum(0);       //评论数量
            sku.setSaleNum(0);          //销售数量


            if (sku.getSpec()==null||"".equals(sku.getSpec())){
                sku.setSpec("{}");
            }

            skuMapper.insert(sku);
        }
    }

    @Override
    public Goods findGoodsById(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);

        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId", id);
        List<Sku> skuList = skuMapper.selectByExample(example);

        Goods goods = new Goods();
        goods.setSpu(spu);
        goods.setSkuList(skuList);
        return goods;
    }

    @Override
    @Transactional
    public void audit(String id, String status, String message) {
        //根据id获取spu
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //获取spu的状态
        spu.setStatus(status);
        if ("1".equals(status)){
            //如果审核通过 则商品自动上架
            spu.setIsMarketable("1");
        }

        //记录商品的审核信息
        AuditLog auditLog = new AuditLog();
        //分布式id
        auditLog.setId(String.valueOf(idWorker.nextId()));
        auditLog.setName(spu.getName());
        auditLog.setStatus(status);
        auditLog.setAuditTime(new Date());
        auditLog.setAuditAdmin("dai");
        auditLog.setAuditMessage(message);

        //插入数据库
        auditLogMapper.insert(auditLog);
    }

    /**
     * 查询商品回收站里的商品
     * @return
     */
    @Override
    public List<Goods> findDeleteGoods() {
        ArrayList<Goods> goods = new ArrayList<>();

        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDelete","1");

        List<Spu> spus = spuMapper.selectByExample(example);
        for (Spu spu : spus) {
            Goods goods1 = new Goods();
            goods1.setSpu(spu);

            List<Sku> skus = skuMapper.selectByExample(spu.getId());
            goods1.setSkuList(skus);

            goods.add(goods1);
        }
        return goods;
    }

    /**
     * 还原商品
     * @param id
     */
    @Override
    public void restoreGoods(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //将是否是删除状态设置为0
        spu.setIsDelete("0");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 删除商品
     * @param id
     */
    @Override
    public void deleteGoods(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //将是否是删除状态设置为0
        spu.setIsDelete("1");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 构建查询条件
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 主键
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                criteria.andLike("id", "%" + searchMap.get("id") + "%");
            }
            // 货号
            if (searchMap.get("sn") != null && !"".equals(searchMap.get("sn"))) {
                criteria.andLike("sn", "%" + searchMap.get("sn") + "%");
            }
            // SPU名
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                criteria.andLike("name", "%" + searchMap.get("name") + "%");
            }
            // 副标题
            if (searchMap.get("caption") != null && !"".equals(searchMap.get("caption"))) {
                criteria.andLike("caption", "%" + searchMap.get("caption") + "%");
            }
            // 图片
            if (searchMap.get("image") != null && !"".equals(searchMap.get("image"))) {
                criteria.andLike("image", "%" + searchMap.get("image") + "%");
            }
            // 图片列表
            if (searchMap.get("images") != null && !"".equals(searchMap.get("images"))) {
                criteria.andLike("images", "%" + searchMap.get("images") + "%");
            }
            // 售后服务
            if (searchMap.get("saleService") != null && !"".equals(searchMap.get("saleService"))) {
                criteria.andLike("saleService", "%" + searchMap.get("saleService") + "%");
            }
            // 介绍
            if (searchMap.get("introduction") != null && !"".equals(searchMap.get("introduction"))) {
                criteria.andLike("introduction", "%" + searchMap.get("introduction") + "%");
            }
            // 规格列表
            if (searchMap.get("specItems") != null && !"".equals(searchMap.get("specItems"))) {
                criteria.andLike("specItems", "%" + searchMap.get("specItems") + "%");
            }
            // 参数列表
            if (searchMap.get("paraItems") != null && !"".equals(searchMap.get("paraItems"))) {
                criteria.andLike("paraItems", "%" + searchMap.get("paraItems") + "%");
            }
            // 是否上架
            if (searchMap.get("isMarketable") != null && !"".equals(searchMap.get("isMarketable"))) {
                criteria.andLike("isMarketable", "%" + searchMap.get("isMarketable") + "%");
            }
            // 是否启用规格
            if (searchMap.get("isEnableSpec") != null && !"".equals(searchMap.get("isEnableSpec"))) {
                criteria.andLike("isEnableSpec", "%" + searchMap.get("isEnableSpec") + "%");
            }
            // 是否删除
            if (searchMap.get("isDelete") != null && !"".equals(searchMap.get("isDelete"))) {
                criteria.andLike("isDelete", "%" + searchMap.get("isDelete") + "%");
            }
            // 审核状态
            if (searchMap.get("status") != null && !"".equals(searchMap.get("status"))) {
                criteria.andLike("status", "%" + searchMap.get("status") + "%");
            }

            // 品牌ID
            if (searchMap.get("brandId") != null) {
                criteria.andEqualTo("brandId", searchMap.get("brandId"));
            }
            // 一级分类
            if (searchMap.get("category1Id") != null) {
                criteria.andEqualTo("category1Id", searchMap.get("category1Id"));
            }
            // 二级分类
            if (searchMap.get("category2Id") != null) {
                criteria.andEqualTo("category2Id", searchMap.get("category2Id"));
            }
            // 三级分类
            if (searchMap.get("category3Id") != null) {
                criteria.andEqualTo("category3Id", searchMap.get("category3Id"));
            }
            // 模板ID
            if (searchMap.get("templateId") != null) {
                criteria.andEqualTo("templateId", searchMap.get("templateId"));
            }
            // 运费模板id
            if (searchMap.get("freightId") != null) {
                criteria.andEqualTo("freightId", searchMap.get("freightId"));
            }
            // 销量
            if (searchMap.get("saleNum") != null) {
                criteria.andEqualTo("saleNum", searchMap.get("saleNum"));
            }
            // 评论数
            if (searchMap.get("commentNum") != null) {
                criteria.andEqualTo("commentNum", searchMap.get("commentNum"));
            }

        }
        return example;
    }

}
