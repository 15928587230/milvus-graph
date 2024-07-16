package com.owinfo.milvus.document.milvus.domain;

import dev.langchain4j.service.UserMessage;

public interface ExtractDocument {
    @UserMessage("从{{it}}中提取: title 标题, keyword 关键词, brief 摘要。")
    DocumentKeyword extractFrom(String text);
}
