package com.example.book.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNotices is a Querydsl query type for Notices
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotices extends EntityPathBase<Notices> {

    private static final long serialVersionUID = 288981381L;

    public static final QNotices notices = new QNotices("notices");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final StringPath nBContent = createString("nBContent");

    public final NumberPath<Long> nBId = createNumber("nBId", Long.class);

    public final StringPath nBTitle = createString("nBTitle");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final NumberPath<Long> userNo = createNumber("userNo", Long.class);

    public QNotices(String variable) {
        super(Notices.class, forVariable(variable));
    }

    public QNotices(Path<? extends Notices> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNotices(PathMetadata metadata) {
        super(Notices.class, metadata);
    }

}

