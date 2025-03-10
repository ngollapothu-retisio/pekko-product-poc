package pekko.product.api.v1.converter;

import pekko.product.api.v1.response.GetProductResponse;
import pekko.product.application.domain.Product;
import pekko.product.application.reply.ProductReply;
import static pekko.product.application.constant.ProductConstant.INVALID;
import static pekko.product.application.constant.ProductConstant.SUCCESS;
public interface ProductReplyToGetResponse {
    static GetProductResponse convert(ProductReply reply){
        if(reply instanceof ProductReply.InvalidCommand){
            ProductReply.InvalidCommand invalidCommand = (ProductReply.InvalidCommand)reply;
            return new GetProductResponse(invalidCommand.code(), invalidCommand.message());
        } else if(reply instanceof ProductReply.SuccessCommand){
            ProductReply.SuccessCommand successCommand = (ProductReply.SuccessCommand)reply;
            return new GetProductResponse(successCommand.code(), successCommand.message());
        } else if(reply instanceof ProductReply.Domain){
            Product product = ((ProductReply.Domain)reply).product();
            return new GetProductResponse(product.id(), product.name(), product.active(), product.deleted());
        }
        return new GetProductResponse(INVALID, "Something is wrong");
    }
}
