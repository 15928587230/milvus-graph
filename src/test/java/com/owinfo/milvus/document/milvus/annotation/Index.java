package com.owinfo.milvus.document.milvus.annotation;

import io.milvus.v2.common.IndexParam;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {

    String name();
    IndexParam.IndexType type();

    String metricType() default "";
}
