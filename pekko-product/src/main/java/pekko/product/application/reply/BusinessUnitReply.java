package pekko.product.application.reply;

import pekko.product.application.domain.BusinessUnit;
import pekko.product.application.domain.Product;
import pekko.product.application.serializer.JsonSerializable;

public interface BusinessUnitReply extends JsonSerializable {
    record InvalidCommand(Integer code, String message) implements BusinessUnitReply {
    }
    record SuccessCommand(Integer code, String message) implements BusinessUnitReply {
    }
    record Domain(BusinessUnit businessUnit) implements BusinessUnitReply {
    }
}
