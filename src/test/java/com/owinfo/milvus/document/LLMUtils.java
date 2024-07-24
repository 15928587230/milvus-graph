package com.owinfo.milvus.document;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParserFactory;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.qianfan.QianfanChatModel;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

public class LLMUtils {

    /**
     * 千帆聊天模型
     *
     * @return
     */
    public static QianfanChatModel qianfan() {
        QianfanChatModel chatModel = QianfanChatModel.builder()
                .apiKey("xxx")
                .secretKey("17o1cZgs1oByri496ZpJoUjt1naSgJPH")
                .modelName("Yi-34B-Chat")
                .build();
        return chatModel;
    }

    public static List<TextSegment> createSegment(String filePath) throws IOException {
        ApacheTikaDocumentParserFactory parserFactory = new ApacheTikaDocumentParserFactory();
        DocumentParser documentParser = parserFactory.create();
        ClassPathResource resource = new ClassPathResource(filePath);
        Document document = documentParser.parse(resource.getInputStream());
        DocumentSplitter splitter = DocumentSplitters.recursive(600, 50);
        List<TextSegment> textSegments = splitter.split(document);
        // 少于120的分段文本没有参考意义
        return textSegments.stream().filter(textSegment -> textSegment.text().length() > 120).toList();
    }
}
