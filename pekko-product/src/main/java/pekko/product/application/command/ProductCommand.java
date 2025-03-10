package pekko.product.application.command;

import org.apache.pekko.actor.typed.ActorRef;
import pekko.product.application.reply.ProductReply;
import pekko.product.application.serializer.JsonSerializable;


public interface ProductCommand extends JsonSerializable {

    record Create(String id, String name, Boolean active, ActorRef<ProductReply> replyTo) implements ProductCommand {
    }
    record Update(String id, String name, Boolean active, ActorRef<ProductReply> replyTo) implements ProductCommand {
    }
    record Patch(String id, String name, Boolean active, ActorRef<ProductReply> replyTo) implements ProductCommand {
    }
    record Delete(String id, ActorRef<ProductReply> replyTo) implements ProductCommand {
    }
    record Get(String id, ActorRef<ProductReply> replyTo) implements ProductCommand {
    }

}