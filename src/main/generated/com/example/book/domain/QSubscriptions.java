package com.example.book.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSubscriptions is a Querydsl query type for Subscriptions
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSubscriptions extends EntityPathBase<Subscriptions> {

    private static final long serialVersionUID = -426233760L;

    public static final QSubscriptions subscriptions = new QSubscriptions("subscriptions");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final BooleanPath isSub = createBoolean("isSub");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final NumberPath<Long> subAmount = createNumber("subAmount", Long.class);

    public final NumberPath<Long> subCategory = createNumber("subCategory", Long.class);

    public final NumberPath<Long> subId = createNumber("subId", Long.class);

    public final BooleanPath subNotice = createBoolean("subNotice");

    public final NumberPath<Integer> subPayDate = createNumber("subPayDate", Integer.class);

    public final EnumPath<SubPeriodUnit> subPeriodUnit = createEnum("subPeriodUnit", SubPeriodUnit.class);

    public final NumberPath<Integer> subPeriodValue = createNumber("subPeriodValue", Integer.class);

    public final StringPath subTitle = createString("subTitle");

    public final NumberPath<Long> userNo = createNumber("userNo", Long.class);

    public QSubscriptions(String variable) {
        super(Subscriptions.class, forVariable(variable));
    }

    public QSubscriptions(Path<? extends Subscriptions> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSubscriptions(PathMetadata metadata) {
        super(Subscriptions.class, metadata);
    }

}

