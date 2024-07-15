package com.owinfo.milvus.document.milvus.util;

import com.owinfo.milvus.document.milvus.annotation.Collection;
import com.owinfo.milvus.document.milvus.annotation.Field;
import com.owinfo.milvus.document.milvus.annotation.Index;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.service.collection.response.ListCollectionsResp;
import org.junit.platform.commons.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MilvusUtils {

    public static  <T> void createCollection(MilvusClientV2 clientV2, Class<T> clazz) {
        Collection collectionAnno = clazz.getAnnotation(Collection.class);
        if (collectionAnno == null) return;

        ListCollectionsResp listCollectionsResp = clientV2.listCollections();
        List<String> collectionNames = listCollectionsResp.getCollectionNames();
        if (collectionNames.contains(collectionAnno.name())) return;

        List<CreateCollectionReq.FieldSchema> fieldSchemaList = new ArrayList<>();
        List<IndexParam> indexParamList = new ArrayList<>();
        java.lang.reflect.Field[] fields = clazz.getClass().getFields();

        for (java.lang.reflect.Field field : fields) {
            Field filedAnno = field.getAnnotation(Field.class);
            Index indexAnno = field.getAnnotation(Index.class);

            if (filedAnno == null) continue;
            CreateCollectionReq.FieldSchema.FieldSchemaBuilder<?, ?> builder = CreateCollectionReq.FieldSchema.builder();
            builder.isPrimaryKey(filedAnno.primary())
                    .autoID(filedAnno.autoId())
                    .name(filedAnno.name())
                    .dataType(filedAnno.type())
                    .description(filedAnno.description());
            if (StringUtils.isNotBlank(filedAnno.maxLen())) {
                builder.maxLength(Integer.parseInt(filedAnno.maxLen()));
            }
            fieldSchemaList.add(builder.build());

            if (indexAnno == null) continue;
            IndexParam.IndexParamBuilder<?, ?> indexParamBuilder = IndexParam.builder().fieldName(field.getName())
                    .indexName(indexAnno.name())
                    .indexType(indexAnno.type());
            if (StringUtils.isNotBlank(indexAnno.metricType())) {
                IndexParam.MetricType metricType = IndexParam.MetricType.valueOf(indexAnno.metricType());
                indexParamBuilder.metricType(metricType);
            }
            indexParamList.add(indexParamBuilder.build());
        }

        CreateCollectionReq.CollectionSchema collectionSchema = CreateCollectionReq
                .CollectionSchema.builder().fieldSchemaList(fieldSchemaList).build();

        CreateCollectionReq createCollectionReq = CreateCollectionReq
                .builder()
                .collectionName(collectionAnno.name())
                .indexParams(indexParamList)
                .description(collectionAnno.description())
                .collectionSchema(collectionSchema)
                .dimension(128)
                .build();
        clientV2.createCollection(createCollectionReq);
    }
}
