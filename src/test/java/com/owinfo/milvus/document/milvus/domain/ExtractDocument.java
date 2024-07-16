package com.owinfo.milvus.document.milvus.domain;

import dev.langchain4j.service.UserMessage;

public interface ExtractDocument {
    @UserMessage("从{{it}}中提取title标题、keyword关键词、brief摘要等信息并赋值，超过3秒放弃提取赋值")
    DocumentKeyword extractFrom(String text);
}
