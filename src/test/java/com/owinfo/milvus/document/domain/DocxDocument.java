package com.owinfo.milvus.document.domain;

import com.owinfo.milvus.document.annotation.Collection;
import com.owinfo.milvus.document.annotation.Field;
import com.owinfo.milvus.document.annotation.Index;
import io.milvus.v2.common.DataType;
import io.milvus.v2.common.IndexParam;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Collection(name = "lawDocument", description = "法律文档数据库")
public class DocxDocument {
    @Field(primary = true, autoId = true, name = "id", type = DataType.Int64, description = "分段文档ID")
    private Long id;

    @Field(name = "documentId", type = DataType.VarChar, maxLen = "32", description = "文档ID")
    private String documentId;

    @Index(name = "titleIndex", type = IndexParam.IndexType.INVERTED)
    @Field(name = "title", type = DataType.VarChar, maxLen = "100", description = "标题")
    private String title;

    @Index(name = "briefIndex", type = IndexParam.IndexType.INVERTED)
    @Field(name = "brief", type = DataType.VarChar, maxLen = "255", description = "摘要")
    private String brief;

    @Index(name = "keywordIndex", type = IndexParam.IndexType.INVERTED)
    @Field(name = "keyword", type = DataType.VarChar, maxLen = "100", description = "关键词")
    private String keyword;

    @Index(name = "contentVectorIndex", type = IndexParam.IndexType.IVF_PQ, metricType = "L2")
    @Field(name = "contentVector", type = DataType.FloatVector, description = "内容向量")
    private float[] contentVector;
}