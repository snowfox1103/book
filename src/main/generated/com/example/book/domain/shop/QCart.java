package com.example.book.domain.shop;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCart is a Querydsl query type for Cart
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCart extends EntityPathBase<Cart> {

    private static final long serialVersionUID = 1075003842L;

    public static final QCart cart = new QCart("cart");

    public final com.example.book.domain.common.QBaseEntity _super = new com.example.book.domain.common.QBaseEntity(this);

    public final NumberPath<Long> cartId = createNumber("cartId", Long.class);

    public final NumberPath<Long> itemCount = createNumber("itemCount", Long.class);

    public final NumberPath<Long> itemId = createNumber("itemId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final NumberPath<Long> userNo = createNumber("userNo", Long.class);

    public QCart(String variable) {
        super(Cart.class, forVariable(variable));
    }

    public QCart(Path<? extends Cart> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCart(PathMetadata metadata) {
        super(Cart.class, metadata);
    }

}

