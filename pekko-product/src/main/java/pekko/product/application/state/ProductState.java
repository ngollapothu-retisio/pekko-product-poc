package pekko.product.application.state;

import pekko.product.application.serializer.JsonSerializable;
import pekko.product.application.domain.Product;

import java.util.Optional;

public record ProductState(Optional<Product> product) implements JsonSerializable {

    public static final ProductState EMPTY = new ProductState(Optional.empty());

}
