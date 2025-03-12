package pekko.product.application.converter;

import pekko.product.application.domain.Product;
import pekko.product.application.event.ProductEvent;

import java.util.Optional;

public interface ProductEventToDomain {
    static Product convert(ProductEvent.Created event){
        return new Product(
                event.id(),
                event.name(),
                Optional.ofNullable(event.active()).orElseGet(()->false),
                false
        );
    }
    static Product convert(ProductEvent.Updated event){
        return new Product(
                event.id(),
                event.name(),
                Optional.ofNullable(event.active()).orElseGet(()->false),
                false
        );
    }

}
