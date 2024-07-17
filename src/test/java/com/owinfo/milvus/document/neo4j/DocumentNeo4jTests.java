package com.owinfo.milvus.document.neo4j;

import com.owinfo.milvus.MilvusGraphApplicationTests;
import org.junit.Test;
import org.neo4j.driver.*;

// 构建知识图谱测试
public class DocumentNeo4jTests extends MilvusGraphApplicationTests {

    private final String url = "bolt://192.168.0.120:7687";
    private final String username = "neo4j";
    private final String password = "123456Pjj";

    protected Driver getNeo4jDriver() {
        return GraphDatabase.driver(url, AuthTokens.basic(username, password));
    }

    protected Session getNeo4jSession() {
        return getNeo4jDriver().session();
    }

    @Test
    public void getNeo4JConnection() {
        Driver driver = GraphDatabase.driver(url, AuthTokens.basic(password, password));
        ExecutableQuery executableQuery = driver.executableQuery("CALL db.labels()");
        EagerResult execute = executableQuery.execute();
        execute.records().forEach(System.out::println);
    }
}
