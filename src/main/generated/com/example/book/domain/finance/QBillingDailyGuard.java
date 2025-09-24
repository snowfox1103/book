package com.example.book.domain.finance;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBillingDailyGuard is a Querydsl query type for BillingDailyGuard
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBillingDailyGuard extends EntityPathBase<BillingDailyGuard> {

    private static final long serialVersionUID = 7896637L;

    public static final QBillingDailyGuard billingDailyGuard = new QBillingDailyGuard("billingDailyGuard");

    public final DatePath<java.time.LocalDate> lastRunDate = createDate("lastRunDate", java.time.LocalDate.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userNo = createNumber("userNo", Long.class);

    public QBillingDailyGuard(String variable) {
        super(BillingDailyGuard.class, forVariable(variable));
    }

    public QBillingDailyGuard(Path<? extends BillingDailyGuard> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBillingDailyGuard(PathMetadata metadata) {
        super(BillingDailyGuard.class, metadata);
    }

}

