package com.example.book.domain.shop;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QItems is a Querydsl query type for Items
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItems extends EntityPathBase<Items> {

    private static final long serialVersionUID = -1028524706L;

    public static final QItems items = new QItems("items");

    public final com.example.book.domain.common.QBaseEntity _super = new com.example.book.domain.common.QBaseEntity(this);

    public final NumberPath<Long> itemId = createNumber("itemId", Long.class);

    public final StringPath itemImage = createString("itemImage");

    public final StringPath itemName = createString("itemName");

    public final NumberPath<Long> itemPrice = createNumber("itemPrice", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public QItems(String variable) {
        super(Items.class, forVariable(variable));
    }

    public QItems(Path<? extends Items> path) {
        super(path.getType(), path.getMetadata());
    }

    public QItems(PathMetadata metadata) {
        super(Items.class, metadata);
    }

}

