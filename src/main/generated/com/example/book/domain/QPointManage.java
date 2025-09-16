package com.example.book.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPointManage is a Querydsl query type for PointManage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPointManage extends EntityPathBase<PointManage> {

    private static final long serialVersionUID = 430779135L;

    public static final QPointManage pointManage = new QPointManage("pointManage");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final NumberPath<Long> PMCat = createNumber("PMCat", Long.class);

    public final NumberPath<Long> PMId = createNumber("PMId", Long.class);

    public final NumberPath<Long> PMMax = createNumber("PMMax", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public QPointManage(String variable) {
        super(PointManage.class, forVariable(variable));
    }

    public QPointManage(Path<? extends PointManage> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPointManage(PathMetadata metadata) {
        super(PointManage.class, metadata);
    }

}

