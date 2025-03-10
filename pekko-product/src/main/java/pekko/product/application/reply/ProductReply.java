package pekko.product.application.reply;

import pekko.product.application.domain.Product;
import pekko.product.application.serializer.JsonSerializable;

public interface ProductReply extends JsonSerializable {
    record InvalidCommand(Integer code, String message) implements ProductReply {
    }
    record SuccessCommand(Integer code, String message) implements ProductReply {
    }
    record Domain(Product product) implements ProductReply {
    }
}
