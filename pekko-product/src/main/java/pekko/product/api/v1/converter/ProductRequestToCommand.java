package pekko.product.api.v1.converter;

import org.apache.pekko.actor.typed.ActorRef;
import pekko.product.api.v1.request.CreateProductRequest;
import pekko.product.api.v1.request.PatchProductRequest;
import pekko.product.api.v1.request.UpdateProductRequest;
import pekko.product.application.command.ProductCommand;
import pekko.product.application.reply.ProductReply;

public interface ProductRequestToCommand {
    static ProductCommand.Create convert(CreateProductRequest request, ActorRef<ProductReply> replyTo){
        return new ProductCommand.Create(
                request.id(),
                request.name(),
                request.active(),
                replyTo
        );
    }
    static ProductCommand.Update convert(UpdateProductRequest request, ActorRef<ProductReply> replyTo){
        return new ProductCommand.Update(
                request.id(),
                request.name(),
                request.active(),
                replyTo
        );
    }
    static ProductCommand.Patch convert(PatchProductRequest request, String id, ActorRef<ProductReply> replyTo){
        return new ProductCommand.Patch(
                id,
                request.name(),
                request.active(),
                replyTo
        );
    }
    static ProductCommand.Delete convert(String id, ActorRef<ProductReply> replyTo){
        return new ProductCommand.Delete(
                id,
                replyTo
        );
    }
}
