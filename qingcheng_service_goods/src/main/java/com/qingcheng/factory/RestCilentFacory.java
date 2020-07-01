package com.qingcheng.factory;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @author 戴金华
 * @date 2019-11-25 18:23
 */
public class RestCilentFacory {

    public static RestHighLevelClient getRestHightLevalCilent(String host,int port){
        HttpHost http = new HttpHost(host, port, "http");
        //rest构建器
        RestClientBuilder builder = RestClient.builder(http);
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(builder);
        return restHighLevelClient;
    }
}
