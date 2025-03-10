package pekko.product.application.converter;

import pekko.product.application.domain.Product;
import pekko.product.application.event.ProductEvent;
import pekko.product.application.state.ProductState;

import java.util.Optional;

public interface ProductEventToState {
    static ProductState convert(ProductEvent.Created event){
        return new ProductState(
                Optional.of(
                        new Product(
                                event.id(),
                                event.name(),
                                event.active(),
                                false
                        )
                )
        );
    }
    static ProductState convert(ProductEvent.Updated event){
        return new ProductState(
                Optional.of(
                        new Product(
                                event.id(),
                                event.name(),
                                event.active(),
                                false
                        )
                )
        );
    }
    static ProductState convert(ProductState state, ProductEvent.Patched event){
        if(state.product().isPresent()){
            Product product = state.product().get();
            return new ProductState(
                    Optional.of(
                            new Product(
                                    event.id(),
                                    Optional.ofNullable(event.name()).orElseGet(()->product.name()),
                                    Optional.ofNullable(event.active()).orElseGet(()->product.active()),
                                    false
                            )
                    )
            );
        }
        return state;
    }
    static ProductState convert(ProductEvent.Deleted event){
        return ProductState.EMPTY;
    }
}
