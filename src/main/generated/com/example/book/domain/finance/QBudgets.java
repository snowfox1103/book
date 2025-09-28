package com.example.book.domain.finance;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBudgets is a Querydsl query type for Budgets
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBudgets extends EntityPathBase<Budgets> {

    private static final long serialVersionUID = 354595492L;

    public static final QBudgets budgets = new QBudgets("budgets");

    public final com.example.book.domain.common.QBaseEntity _super = new com.example.book.domain.common.QBaseEntity(this);

    public final NumberPath<Long> budAmount = createNumber("budAmount", Long.class);

    public final NumberPath<Long> budCategory = createNumber("budCategory", Long.class);

    public final NumberPath<Long> budCurrent = createNumber("budCurrent", Long.class);

    public final NumberPath<Long> budgetId = createNumber("budgetId", Long.class);

    public final BooleanPath budIsOver = createBoolean("budIsOver");

    public final NumberPath<Integer> budMonth = createNumber("budMonth", Integer.class);

    public final BooleanPath budNotice = createBoolean("budNotice");

    public final NumberPath<Integer> budThreshold = createNumber("budThreshold", Integer.class);

    public final NumberPath<Integer> budYear = createNumber("budYear", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final NumberPath<Long> userNo = createNumber("userNo", Long.class);

    public QBudgets(String variable) {
        super(Budgets.class, forVariable(variable));
    }

    public QBudgets(Path<? extends Budgets> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBudgets(PathMetadata metadata) {
        super(Budgets.class, metadata);
    }

}

