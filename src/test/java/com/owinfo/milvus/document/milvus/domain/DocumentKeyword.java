package com.owinfo.milvus.document.milvus.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DocumentKeyword {
    private String keyword;
    private String title;
    private String brief;
}
