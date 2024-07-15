package com.owinfo.milvus.document;

import com.owinfo.milvus.MilvusGraphApplicationTests;
import com.owinfo.milvus.document.domain.DocxDocument;
import com.owinfo.milvus.document.util.MilvusUtils;
import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class DocumentMilvusTests extends MilvusGraphApplicationTests {
    private MilvusClientV2 clientV2;

    @Before
    public void initClient() {
        log.info("初始化 Milvus Java Client V2......");
        ConnectConfig build = ConnectConfig.builder()
                .uri("http://192.168.0.112:19530")
                .build();
        clientV2 = new MilvusClientV2(build);

        // 法律文档，我们需要的索引和元字段建立
        // 选择什么样的标量索引
        // 选择什么样的向量索引和度量方法
        // 开源嵌入模型区别测试
        MilvusUtils.createCollection(clientV2, DocxDocument.class);
    }

    // 测试客户端连接
    @Test
    public void testInitClient() {
        log.info("获取Milvus用户，{}", clientV2.listUsers());
    }

    // 测试LangChain4J文档处理、关键词摘要提取、模型向量化使用
    @Test
    public void testLangChain4J() {

    }

    // 测试LangChain4J数据处理功能、文档插入功能
    @Test
    public void testInsertDocument() {

    }

    // 测试全文检索效果
    @Test
    public void testSearchFullText() {

    }

    // 测试向量检索效果
    @Test
    public void testSearchEmbedded() {

    }

    // 测试多重检索效果
    @Test
    public void testSearchMixture() {

    }
}
