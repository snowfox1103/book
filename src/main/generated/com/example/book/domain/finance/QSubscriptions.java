package com.example.book.domain.finance;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSubscriptions is a Querydsl query type for Subscriptions
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSubscriptions extends EntityPathBase<Subscriptions> {

    private static final long serialVersionUID = 152554316L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSubscriptions subscriptions = new QSubscriptions("subscriptions");

    public final com.example.book.domain.common.QBaseEntity _super = new com.example.book.domain.common.QBaseEntity(this);

    public final BooleanPath anchorToMonthEnd = createBoolean("anchorToMonthEnd");

    public final QCategories categories;

    public final BooleanPath isSub = createBoolean("isSub");

    public final DateTimePath<java.time.LocalDateTime> lastNotifiedAt = createDateTime("lastNotifiedAt", java.time.LocalDateTime.class);

    public final DatePath<java.time.LocalDate> lastNotifiedFor = createDate("lastNotifiedFor", java.time.LocalDate.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final DatePath<java.time.LocalDate> nextPayDate = createDate("nextPayDate", java.time.LocalDate.class);

    public final NumberPath<Integer> notifyWindowDays = createNumber("notifyWindowDays", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final NumberPath<Long> subAmount = createNumber("subAmount", Long.class);

    public final NumberPath<Long> subId = createNumber("subId", Long.class);

    public final BooleanPath subNotice = createBoolean("subNotice");

    public final NumberPath<Integer> subPayDate = createNumber("subPayDate", Integer.class);

    public final EnumPath<SubPeriodUnit> subPeriodUnit = createEnum("subPeriodUnit", SubPeriodUnit.class);

    public final NumberPath<Integer> subPeriodValue = createNumber("subPeriodValue", Integer.class);

    public final StringPath subTitle = createString("subTitle");

    public final com.example.book.domain.user.QUsers users;

    public QSubscriptions(String variable) {
        this(Subscriptions.class, forVariable(variable), INITS);
    }

    public QSubscriptions(Path<? extends Subscriptions> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSubscriptions(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSubscriptions(PathMetadata metadata, PathInits inits) {
        this(Subscriptions.class, metadata, inits);
    }

    public QSubscriptions(Class<? extends Subscriptions> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.categories = inits.isInitialized("categories") ? new QCategories(forProperty("categories"), inits.get("categories")) : null;
        this.users = inits.isInitialized("users") ? new com.example.book.domain.user.QUsers(forProperty("users")) : null;
    }

}

