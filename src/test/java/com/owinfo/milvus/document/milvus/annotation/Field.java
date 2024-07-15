package com.owinfo.milvus.document.milvus.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {

    /**
     * 是否主键
     * @return
     */
    boolean primary() default false;

    /**
     * 是否自增
     * @return
     */
    boolean autoId() default false;
    /**
     * 字段在集合种的名字
     * @return
     */
    String name();

    /**
     * 字段类型 DataType
     *
     * @return
     */
    io.milvus.v2.common.DataType type();
    String maxLen() default "";
    String description() default "";
}
