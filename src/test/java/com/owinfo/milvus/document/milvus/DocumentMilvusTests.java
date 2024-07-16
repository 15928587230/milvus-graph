package com.owinfo.milvus.document.milvus;

import com.owinfo.milvus.MilvusGraphApplicationTests;
import com.owinfo.milvus.document.milvus.domain.DocumentKeyword;
import com.owinfo.milvus.document.milvus.domain.DocxDocument;
import com.owinfo.milvus.document.milvus.domain.ExtractDocument;
import com.owinfo.milvus.document.milvus.util.MilvusUtils;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParserFactory;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.jina.JinaEmbeddingModel;
import dev.langchain4j.model.jina.JinaScoringModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.qianfan.QianfanChatModel;
import dev.langchain4j.model.qianfan.QianfanEmbeddingModel;
import dev.langchain4j.service.AiServices;
import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class DocumentMilvusTests extends MilvusGraphApplicationTests {

    private boolean notInit = true;
    private MilvusClientV2 clientV2;

    protected QianfanChatModel getQianfanChatModel() {
        QianfanChatModel chatModel = QianfanChatModel.builder()
                .apiKey("xxx")
                .secretKey("17o1cZgs1oByri496ZpJoUjt1naSgJPH")
                .modelName("Yi-34B-Chat")
                .build();
        return chatModel;
    }

    protected QianfanEmbeddingModel getQianfanEmbeddingModel() {
        QianfanEmbeddingModel embeddingModel = QianfanEmbeddingModel.builder()
                .apiKey("xxx")
                .secretKey("gMZhB7NlJInG6eluhG3dy9SOqWY7VEYf")
                // 可以用要收费
                .modelName("Embedding-V1")
                .build();
        return embeddingModel;
    }

    protected JinaEmbeddingModel getJinaEmbeddingModel() {
        JinaEmbeddingModel jinaEmbeddingModel = JinaEmbeddingModel
                .withApiKey("jina_e467a53ef0d44efc983388adb25671aeop4xLrutrhmD2hRGHDj-xEV_oNV6");
        return jinaEmbeddingModel;
    }

    protected JinaScoringModel getJinaScoringModel() {
        JinaScoringModel jinaScoringModel = JinaScoringModel
                .builder().apiKey("jina_e467a53ef0d44efc983388adb25671aeop4xLrutrhmD2hRGHDj-xEV_oNV6").build();
        return jinaScoringModel;
    }

    @Before
    public void initClient() {
        if (notInit) return;
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

    @Test
    public void testQianfanChat() {
        String generate = getQianfanChatModel().generate("对文章段落进行总结，" + text);
        System.out.println(generate);
    }

    @Test
    public void testOutputObj() {
        ExtractDocument extractDocument = AiServices.create(ExtractDocument.class, getQianfanChatModel());
        DocumentKeyword documentKeyword = extractDocument.extractFrom(text);
        System.out.println(documentKeyword);
    }

    // 测试通过其他方法从文段中提取关键词、标题、摘要
    @Test
    public void testExtractKeyword() {
        DocumentKeyword documentKeyword = extractDocumentKeyword();
        System.out.println(documentKeyword);
    }

    @Test
    public void testQianfanEmbedding() {
        Response<Embedding> embed = getQianfanEmbeddingModel()
                .embed("测试LangChain4J文档处理、关键词摘要提取、模型向量化使用");
        float[] vector = embed.content().vector();
        System.out.println(vector.length);
        for (int i = 0; i < vector.length; i++) {
            System.out.print(vector[i] + " ");
        }
    }

    @Test
    public void testJinaEmbedding() {
        Response<Embedding> embed = getJinaEmbeddingModel().embed("测试LangChain4J文档处理、关键词摘要提取、模型向量化使用");
        float[] vector = embed.content().vector();
        System.out.println(vector.length);
        for (int i = 0; i < vector.length; i++) {
            System.out.print(vector[i] + " ");
        }
    }

    @Test
    public void testJinaReranking() throws IOException {
        JinaScoringModel jinaScoringModel = getJinaScoringModel();
        List<TextSegment> segment = createSegment("中华人民共和国体育法.docx");
        Response<List<Double>> response = jinaScoringModel.scoreAll(segment, "解释一下体育法是什么");
        List<Double> content = response.content();
        content.sort(Comparator.reverseOrder());
        content.forEach(System.out::println);
    }

    // 测试LangCain4J自带的文档解析功能
    @Test
    public void testTikaSplitterDocument() throws Exception {
        List<TextSegment> textSegments = createSegment("中华人民共和国体育法.docx");
        for (TextSegment textSegment : textSegments) {
            System.out.println(textSegment + "\n\n");
        }
    }

    protected DocumentKeyword extractDocumentKeyword() {
        return new DocumentKeyword();
    }

    protected List<TextSegment> createSegment(String filePath) throws IOException {
        ApacheTikaDocumentParserFactory parserFactory = new ApacheTikaDocumentParserFactory();
        DocumentParser documentParser = parserFactory.create();
        ClassPathResource resource = new ClassPathResource(filePath);
        Document document = documentParser.parse(resource.getInputStream());
        DocumentSplitter splitter = DocumentSplitters.recursive(500, 50);
        List<TextSegment> textSegments = splitter.split(document);
        // 少于80的分段文本没有参考意义
        return textSegments.stream().filter(textSegment -> textSegment.text().length() > 80).toList();
    }

    String text = "鼓励学校组建运动队、俱乐部等体育训练组织，开展多种形式的课余体育训练，有条件的可组建高水平运动队，培养竞技体育后备人才。\n" +
            "第二十八条　国家定期举办全国学生（青年）运动会。地方各级人民政府应当结合实际，定期组织本地区学生（青年）运动会。\n" +
            "学校应当每学年至少举办一次全校性的体育运动会。\n" +
            "鼓励公共体育场地设施免费向学校开放使用，为学校举办体育运动会提供服务保障。\n" +
            "鼓励学校开展多种形式的学生体育交流活动。\n" +
            "第二十九条　国家将体育科目纳入初中、高中学业水平考试范围，建立符合学科特点的考核机制。\n" +
            "病残等特殊体质学生的体育科目考核，应当充分考虑其身体";
}
