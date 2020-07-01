# 数据库文档 

### tb_spu  表

| 字段名称 | 字段含义 | 字段类型| 字段长度 | 备注   |
| ---- | ---- | ------- | ---- | ---- |
| id  | 主键 | VARCHAR   |      |      |
| sn  | 货号 | VARCHAR   |      |      |
| name  | SPU名 | VARCHAR   |      |      |
| caption  | 副标题 | VARCHAR   |      |      |
| brand_id  | 品牌ID | INT   |      |      |
| category1_id  | 一级分类 | INT   |      |      |
| category2_id  | 二级分类 | INT   |      |      |
| category3_id  | 三级分类 | INT   |      |      |
| template_id  | 模板ID | INT   |      |      |
| freight_id  | 运费模板id | INT   |      |      |
| image  | 图片 | VARCHAR   |      |      |
| images  | 图片列表 | VARCHAR   |      |      |
| sale_service  | 售后服务 | VARCHAR   |      |      |
| introduction  | 介绍 | TEXT   |      |      |
| spec_items  | 规格列表 | VARCHAR   |      |      |
| para_items  | 参数列表 | VARCHAR   |      |      |
| sale_num  | 销量 | INT   |      |      |
| comment_num  | 评论数 | INT   |      |      |
| is_marketable  | 是否上架 | CHAR   |      |      |
| is_enable_spec  | 是否启用规格 | CHAR   |      |      |
| is_delete  | 是否删除 | CHAR   |      |      |
| status  | 审核状态 | CHAR   |      |      |




### 接口列表

#### 查询所有数据  

##### url 

> /spu/findAll.do

##### http请求方式 

> GET

##### 请求参数

无

##### 返回格式

```
[{
	"id": 主键,
	"sn": 货号,
	"name": SPU名,
	"caption": 副标题,
	"brandId": 品牌ID,
	"category1Id": 一级分类,
	"category2Id": 二级分类,
	"category3Id": 三级分类,
	"templateId": 模板ID,
	"freightId": 运费模板id,
	"image": 图片,
	"images": 图片列表,
	"saleService": 售后服务,
	"introduction": 介绍,
	"specItems": 规格列表,
	"paraItems": 参数列表,
	"saleNum": 销量,
	"commentNum": 评论数,
	"isMarketable": 是否上架,
	"isEnableSpec": 是否启用规格,
	"isDelete": 是否删除,
	"status": 审核状态,

},
.......
]
```



#### 分页查询数据  

##### url

> /spu/findPage.do

##### http请求方式

> GET

##### 请求参数

| 参数   | 必选   | 类型   | 说明    |
| ---- | ---- | ---- | ----- |
| page | true | int  | 页码    |
| size | true | int  | 每页记录数 |

例子：

```
GET /spu/findPage.do?page=1&size=10
```

##### 返回格式

```
{rows:[{
	"id": 主键,
	"sn": 货号,
	"name": SPU名,
	"caption": 副标题,
	"brandId": 品牌ID,
	"category1Id": 一级分类,
	"category2Id": 二级分类,
	"category3Id": 三级分类,
	"templateId": 模板ID,
	"freightId": 运费模板id,
	"image": 图片,
	"images": 图片列表,
	"saleService": 售后服务,
	"introduction": 介绍,
	"specItems": 规格列表,
	"paraItems": 参数列表,
	"saleNum": 销量,
	"commentNum": 评论数,
	"isMarketable": 是否上架,
	"isEnableSpec": 是否启用规格,
	"isDelete": 是否删除,
	"status": 审核状态,

},
.......
],
total:100}
```



#### 条件查询数据  

##### url

> /spu/findList.do

##### http请求方式

> POST

##### 请求参数

| 参数        | 必选   | 类型   | 说明           |
| --------- | ---- | ---- | ------------ |
| searchMap | true | Map  | 条件对象，格式如实体对象 |

例子：

```
POST /spu/findList.do
{
	"id": 主键,
	"sn": 货号,
	"name": SPU名,
	"caption": 副标题,
	"brandId": 品牌ID,
	"category1Id": 一级分类,
	"category2Id": 二级分类,
	"category3Id": 三级分类,
	"templateId": 模板ID,
	"freightId": 运费模板id,
	"image": 图片,
	"images": 图片列表,
	"saleService": 售后服务,
	"introduction": 介绍,
	"specItems": 规格列表,
	"paraItems": 参数列表,
	"saleNum": 销量,
	"commentNum": 评论数,
	"isMarketable": 是否上架,
	"isEnableSpec": 是否启用规格,
	"isDelete": 是否删除,
	"status": 审核状态,

}
```

##### 返回格式

```
[{
	"id": 主键,
	"sn": 货号,
	"name": SPU名,
	"caption": 副标题,
	"brandId": 品牌ID,
	"category1Id": 一级分类,
	"category2Id": 二级分类,
	"category3Id": 三级分类,
	"templateId": 模板ID,
	"freightId": 运费模板id,
	"image": 图片,
	"images": 图片列表,
	"saleService": 售后服务,
	"introduction": 介绍,
	"specItems": 规格列表,
	"paraItems": 参数列表,
	"saleNum": 销量,
	"commentNum": 评论数,
	"isMarketable": 是否上架,
	"isEnableSpec": 是否启用规格,
	"isDelete": 是否删除,
	"status": 审核状态,

},
.......
]
```



#### 条件+分页查询数据  

##### url

> /spu/findPage.do

##### http请求方式

> POST

##### 请求参数

| 参数        | 必选   | 类型   | 说明           |
| --------- | ---- | ---- | ------------ |
| searchMap | true | Map  | 条件对象，格式如实体对象 |
| page      | true | int  | 页码（GET）      |
| size      | true | int  | 每页记录数（GET）   |

例子：

```
POST /spu/findPage.do?page=1&size=10
{
	"id": 主键,
	"sn": 货号,
	"name": SPU名,
	"caption": 副标题,
	"brandId": 品牌ID,
	"category1Id": 一级分类,
	"category2Id": 二级分类,
	"category3Id": 三级分类,
	"templateId": 模板ID,
	"freightId": 运费模板id,
	"image": 图片,
	"images": 图片列表,
	"saleService": 售后服务,
	"introduction": 介绍,
	"specItems": 规格列表,
	"paraItems": 参数列表,
	"saleNum": 销量,
	"commentNum": 评论数,
	"isMarketable": 是否上架,
	"isEnableSpec": 是否启用规格,
	"isDelete": 是否删除,
	"status": 审核状态,

}
```

##### 返回格式

```
{rows:[{
	"id": 主键,
	"sn": 货号,
	"name": SPU名,
	"caption": 副标题,
	"brandId": 品牌ID,
	"category1Id": 一级分类,
	"category2Id": 二级分类,
	"category3Id": 三级分类,
	"templateId": 模板ID,
	"freightId": 运费模板id,
	"image": 图片,
	"images": 图片列表,
	"saleService": 售后服务,
	"introduction": 介绍,
	"specItems": 规格列表,
	"paraItems": 参数列表,
	"saleNum": 销量,
	"commentNum": 评论数,
	"isMarketable": 是否上架,
	"isEnableSpec": 是否启用规格,
	"isDelete": 是否删除,
	"status": 审核状态,

},
.......
],
total:100}
```



#### 根据ID查询数据  

##### url

> /spu/findById.do

##### http请求方式

> GET

##### 请求参数

| 参数   | 必选   | 类型   | 说明   |
| ---- | ---- | ---- | ---- |
| id   | true | int  | 主键  |

例子：

```

```



#### 增加数据  

##### url

> /spu/add.do

##### http请求方式

> POST

##### 请求参数

| 参数       | 必选   | 类型       | 说明   |
| -------- | ---- | -------- | ---- |
| spu | true | spu | 实体对象 |

##### 返回格式

```
{
  code:0,
  message:""
}
```

code为0表示成功，为1表示失败



#### 修改数据  

##### url

> /spu/update.do

##### http请求方式

> POST

##### 请求参数

| 参数       | 必选   | 类型       | 说明   |
| -------- | ---- | -------- | ---- |
| spu | true | spu | 实体对象 |

##### 返回格式：

```
{
  code:0,
  message:""
}
```

code为0表示成功，为1表示失败



#### 删除数据  

##### url

> /spu/delete.do

##### http请求方式

> GET

##### 请求参数

| 参数   | 必选   | 类型   | 说明   |
| ---- | ---- | ---- | ---- |
| id   | true | int  | 主键   |

例子：

```
GET /spu/delete.do?id=1
```

##### 返回格式：

```
{
  code:0,
  message:""
}
```

code为0表示成功，为1表示失败
