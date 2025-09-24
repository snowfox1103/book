package com.example.book.domain.qna;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QQna is a Querydsl query type for Qna
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQna extends EntityPathBase<Qna> {

    private static final long serialVersionUID = 1238888388L;

    public static final QQna qna = new QQna("qna");

    public final com.example.book.domain.common.QBaseEntity _super = new com.example.book.domain.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final BooleanPath qBBlind = createBoolean("qBBlind");

    public final StringPath qBContent = createString("qBContent");

    public final NumberPath<Long> qBId = createNumber("qBId", Long.class);

    public final StringPath qBTitle = createString("qBTitle");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final NumberPath<Long> userNo = createNumber("userNo", Long.class);

    public QQna(String variable) {
        super(Qna.class, forVariable(variable));
    }

    public QQna(Path<? extends Qna> path) {
        super(path.getType(), path.getMetadata());
    }

    public QQna(PathMetadata metadata) {
        super(Qna.class, metadata);
    }

}

