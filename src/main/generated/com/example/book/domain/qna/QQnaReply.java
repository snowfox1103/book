package com.example.book.domain.qna;

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

    private static final long serialVersionUID = 1844342150L;

    public static final QQnaReply qnaReply = new QQnaReply("qnaReply");

    public final com.example.book.domain.common.QBaseEntity _super = new com.example.book.domain.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final StringPath qBContent = createString("qBContent");

    public final NumberPath<Long> qbId = createNumber("qbId", Long.class);

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

