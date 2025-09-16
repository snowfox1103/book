package com.example.book.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QQnaReply is a Querydsl query type for QnaReply
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQnaReply extends EntityPathBase<QnaReply> {

    private static final long serialVersionUID = -150676548L;

    public static final QQnaReply qnaReply = new QQnaReply("qnaReply");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final StringPath qBContent = createString("qBContent");

    public final NumberPath<Long> qBId = createNumber("qBId", Long.class);

    public final NumberPath<Long> qRId = createNumber("qRId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final NumberPath<Long> userNo = createNumber("userNo", Long.class);

    public QQnaReply(String variable) {
        super(QnaReply.class, forVariable(variable));
    }

    public QQnaReply(Path<? extends QnaReply> path) {
        super(path.getType(), path.getMetadata());
    }

    public QQnaReply(PathMetadata metadata) {
        super(QnaReply.class, metadata);
    }

}

