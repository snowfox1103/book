package com.example.book.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCategories is a Querydsl query type for Categories
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCategories extends EntityPathBase<Categories> {

    private static final long serialVersionUID = 199878898L;

    public static final QCategories categories = new QCategories("categories");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final NumberPath<Long> catId = createNumber("catId", Long.class);

    public final StringPath catName = createString("catName");

    public final BooleanPath isSystemDefault = createBoolean("isSystemDefault");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final NumberPath<Long> userNo = createNumber("userNo", Long.class);

    public QCategories(String variable) {
        super(Categories.class, forVariable(variable));
    }

    public QCategories(Path<? extends Categories> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCategories(PathMetadata metadata) {
        super(Categories.class, metadata);
    }

}

