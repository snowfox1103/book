package com.example.book.domain.point;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserPoint is a Querydsl query type for UserPoint
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserPoint extends EntityPathBase<UserPoint> {

    private static final long serialVersionUID = 2033528977L;

    public static final QUserPoint userPoint = new QUserPoint("userPoint");

    public final com.example.book.domain.common.QBaseEntity _super = new com.example.book.domain.common.QBaseEntity(this);

    public final NumberPath<Long> budId = createNumber("budId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final NumberPath<Long> pointAmount = createNumber("pointAmount", Long.class);

    public final NumberPath<Long> pointId = createNumber("pointId", Long.class);

    public final StringPath pointReason = createString("pointReason");

    public final DatePath<java.time.LocalDate> pointStartDate = createDate("pointStartDate", java.time.LocalDate.class);

    public final EnumPath<PointType> pointType = createEnum("pointType", PointType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final NumberPath<Long> userNo = createNumber("userNo", Long.class);

    public QUserPoint(String variable) {
        super(UserPoint.class, forVariable(variable));
    }

    public QUserPoint(Path<? extends UserPoint> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserPoint(PathMetadata metadata) {
        super(UserPoint.class, metadata);
    }

}

