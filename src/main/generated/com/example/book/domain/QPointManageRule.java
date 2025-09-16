package com.example.book.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPointManageRule is a Querydsl query type for PointManageRule
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPointManageRule extends EntityPathBase<PointManageRule> {

    private static final long serialVersionUID = -650600805L;

    public static final QPointManageRule pointManageRule = new QPointManageRule("pointManageRule");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final NumberPath<Long> percentThreshold = createNumber("percentThreshold", Long.class);

    public final NumberPath<Long> PMId = createNumber("PMId", Long.class);

    public final NumberPath<Long> PMRuleId = createNumber("PMRuleId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final NumberPath<Long> rewardAmount = createNumber("rewardAmount", Long.class);

    public final EnumPath<RewardType> rewardType = createEnum("rewardType", RewardType.class);

    public QPointManageRule(String variable) {
        super(PointManageRule.class, forVariable(variable));
    }

    public QPointManageRule(Path<? extends PointManageRule> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPointManageRule(PathMetadata metadata) {
        super(PointManageRule.class, metadata);
    }

}

