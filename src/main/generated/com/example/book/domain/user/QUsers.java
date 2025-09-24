package com.example.book.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUsers is a Querydsl query type for Users
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUsers extends EntityPathBase<Users> {

    private static final long serialVersionUID = 1709111483L;

    public static final QUsers users = new QUsers("users");

    public final com.example.book.domain.common.QBaseEntity _super = new com.example.book.domain.common.QBaseEntity(this);

    public final StringPath email = createString("email");

    public final BooleanPath enabled = createBoolean("enabled");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final StringPath password = createString("password");

    public final StringPath realName = createString("realName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final EnumPath<MemberRole> role = createEnum("role", MemberRole.class);

    public final BooleanPath social = createBoolean("social");

    public final ListPath<com.example.book.domain.finance.Subscriptions, com.example.book.domain.finance.QSubscriptions> subscriptions = this.<com.example.book.domain.finance.Subscriptions, com.example.book.domain.finance.QSubscriptions>createList("subscriptions", com.example.book.domain.finance.Subscriptions.class, com.example.book.domain.finance.QSubscriptions.class, PathInits.DIRECT2);

    public final StringPath userId = createString("userId");

    public final NumberPath<Long> userNo = createNumber("userNo", Long.class);

    public QUsers(String variable) {
        super(Users.class, forVariable(variable));
    }

    public QUsers(Path<? extends Users> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUsers(PathMetadata metadata) {
        super(Users.class, metadata);
    }

}

