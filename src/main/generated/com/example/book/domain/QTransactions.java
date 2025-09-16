package com.example.book.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTransactions is a Querydsl query type for Transactions
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTransactions extends EntityPathBase<Transactions> {

    private static final long serialVersionUID = 352243371L;

    public static final QTransactions transactions = new QTransactions("transactions");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final NumberPath<Long> subId = createNumber("subId", Long.class);

    public final NumberPath<Long> transAmount = createNumber("transAmount", Long.class);

    public final NumberPath<Long> transCategory = createNumber("transCategory", Long.class);

    public final DatePath<java.time.LocalDate> transDate = createDate("transDate", java.time.LocalDate.class);

    public final NumberPath<Long> transId = createNumber("transId", Long.class);

    public final EnumPath<InOrOut> transInOut = createEnum("transInOut", InOrOut.class);

    public final StringPath transMemo = createString("transMemo");

    public final StringPath transTitle = createString("transTitle");

    public final NumberPath<Long> userNo = createNumber("userNo", Long.class);

    public QTransactions(String variable) {
        super(Transactions.class, forVariable(variable));
    }

    public QTransactions(Path<? extends Transactions> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTransactions(PathMetadata metadata) {
        super(Transactions.class, metadata);
    }

}

