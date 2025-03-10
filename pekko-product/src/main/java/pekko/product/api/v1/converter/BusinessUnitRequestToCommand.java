package pekko.product.api.v1.converter;

import org.apache.pekko.actor.typed.ActorRef;
import pekko.product.api.v1.request.CreateBusinessUnitRequest;
import pekko.product.api.v1.request.PatchBusinessUnitRequest;
import pekko.product.api.v1.request.UpdateBusinessUnitRequest;
import pekko.product.application.command.BusinessUnitCommand;
import pekko.product.application.reply.BusinessUnitReply;

public interface BusinessUnitRequestToCommand {
    static BusinessUnitCommand.Create convert(CreateBusinessUnitRequest request, ActorRef<BusinessUnitReply> replyTo){
        return new BusinessUnitCommand.Create(
                request.id(),
                request.name(),
                request.active(),
                replyTo
        );
    }
    static BusinessUnitCommand.Update convert(UpdateBusinessUnitRequest request, ActorRef<BusinessUnitReply> replyTo){
        return new BusinessUnitCommand.Update(
                request.id(),
                request.name(),
                request.active(),
                replyTo
        );
    }
    static BusinessUnitCommand.Patch convert(PatchBusinessUnitRequest request, String id, ActorRef<BusinessUnitReply> replyTo){
        return new BusinessUnitCommand.Patch(
                id,
                request.name(),
                request.active(),
                replyTo
        );
    }
    static BusinessUnitCommand.Delete convert(String id, ActorRef<BusinessUnitReply> replyTo){
        return new BusinessUnitCommand.Delete(
                id,
                replyTo
        );
    }
}
