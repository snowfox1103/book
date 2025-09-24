package com.example.book.domain.finance;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCategories is a Querydsl query type for Categories
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCategories extends EntityPathBase<Categories> {

    private static final long serialVersionUID = 120460678L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCategories categories = new QCategories("categories");

    public final com.example.book.domain.common.QBaseEntity _super = new com.example.book.domain.common.QBaseEntity(this);

    public final NumberPath<Long> catId = createNumber("catId", Long.class);

    public final StringPath catName = createString("catName");

    public final BooleanPath isSystemDefault = createBoolean("isSystemDefault");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final com.example.book.domain.user.QUsers users;

    public QCategories(String variable) {
        this(Categories.class, forVariable(variable), INITS);
    }

    public QCategories(Path<? extends Categories> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCategories(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCategories(PathMetadata metadata, PathInits inits) {
        this(Categories.class, metadata, inits);
    }

    public QCategories(Class<? extends Categories> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.users = inits.isInitialized("users") ? new com.example.book.domain.user.QUsers(forProperty("users")) : null;
    }

}

