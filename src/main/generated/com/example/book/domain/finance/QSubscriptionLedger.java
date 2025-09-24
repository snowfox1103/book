package com.example.book.domain.finance;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSubscriptionLedger is a Querydsl query type for SubscriptionLedger
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSubscriptionLedger extends EntityPathBase<SubscriptionLedger> {

    private static final long serialVersionUID = -1175409072L;

    public static final QSubscriptionLedger subscriptionLedger = new QSubscriptionLedger("subscriptionLedger");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final NumberPath<Long> catId = createNumber("catId", Long.class);

    public final DatePath<java.time.LocalDate> chargeDate = createDate("chargeDate", java.time.LocalDate.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> ledgerId = createNumber("ledgerId", Long.class);

    public final NumberPath<Long> subId = createNumber("subId", Long.class);

    public final NumberPath<Long> userNo = createNumber("userNo", Long.class);

    public QSubscriptionLedger(String variable) {
        super(SubscriptionLedger.class, forVariable(variable));
    }

    public QSubscriptionLedger(Path<? extends SubscriptionLedger> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSubscriptionLedger(PathMetadata metadata) {
        super(SubscriptionLedger.class, metadata);
    }

}

