package pekko.product.application.command;

import org.apache.pekko.actor.typed.ActorRef;
import pekko.product.application.reply.BusinessUnitReply;
import pekko.product.application.serializer.JsonSerializable;

public interface BusinessUnitCommand extends JsonSerializable {
    record Create(String id, String name, Boolean active, ActorRef<BusinessUnitReply> replyTo) implements BusinessUnitCommand {
    }
    record Update(String id, String name, Boolean active, ActorRef<BusinessUnitReply> replyTo) implements BusinessUnitCommand {
    }
    record Patch(String id, String name, Boolean active, ActorRef<BusinessUnitReply> replyTo) implements BusinessUnitCommand {
    }
    record Delete(String id, ActorRef<BusinessUnitReply> replyTo) implements BusinessUnitCommand {
    }
    record Get(String id, ActorRef<BusinessUnitReply> replyTo) implements BusinessUnitCommand {
    }
}