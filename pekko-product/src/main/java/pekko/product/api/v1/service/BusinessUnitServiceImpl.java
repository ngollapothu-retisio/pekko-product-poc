package pekko.product.api.v1.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.typed.javadsl.Adapter;
import org.apache.pekko.cluster.sharding.typed.javadsl.ClusterSharding;
import org.apache.pekko.cluster.sharding.typed.javadsl.EntityRef;
import pekko.product.api.v1.converter.BusinessUnitReplyToGetResponse;
import pekko.product.api.v1.converter.BusinessUnitRequestToCommand;
import pekko.product.api.v1.request.CreateBusinessUnitRequest;
import pekko.product.api.v1.request.PatchBusinessUnitRequest;
import pekko.product.api.v1.request.UpdateBusinessUnitRequest;
import pekko.product.api.v1.response.GetBusinessUnitResponse;
import pekko.product.application.command.BusinessUnitCommand;
import pekko.product.application.entity.BusinessUnitEntity;
import pekko.product.application.reply.BusinessUnitReply;

import javax.inject.Inject;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Slf4j
public class BusinessUnitServiceImpl implements BusinessUnitService {

    private final ClusterSharding clusterSharding;

    @Inject
    public BusinessUnitServiceImpl(ActorSystem classicActorSystem){
        org.apache.pekko.actor.typed.ActorSystem<Void> typedActorSystem = Adapter.toTyped(classicActorSystem);
        this.clusterSharding = ClusterSharding.get(typedActorSystem);

        BusinessUnitEntity.init(clusterSharding);

        log.info("pekko actors registration is processed");
    }

    private static final Duration askTimeout = Duration.ofSeconds(10);

    public EntityRef<BusinessUnitCommand> getBusinessUnitEntityRef(String entityId) {
        return clusterSharding.entityRefFor(BusinessUnitEntity.ENTITY_TYPE_KEY, entityId);
    }

    public CompletionStage<BusinessUnitReply> getBusinessUnitEntity(String id) {
        return getBusinessUnitEntityRef(id).<BusinessUnitReply>ask(replyTo -> new BusinessUnitCommand.Get(id, replyTo), askTimeout);
    }
    private CompletionStage<BusinessUnitReply> createBusinessUnitEntity(CreateBusinessUnitRequest request, String id) {
        return getBusinessUnitEntityRef(id).<BusinessUnitReply>ask(replyTo -> BusinessUnitRequestToCommand.convert(request, replyTo), askTimeout);
    }
    private CompletionStage<BusinessUnitReply> updateBusinessUnitEntity(UpdateBusinessUnitRequest request, String id) {
        return getBusinessUnitEntityRef(id).<BusinessUnitReply>ask(replyTo -> BusinessUnitRequestToCommand.convert(request, replyTo), askTimeout);
    }
    private CompletionStage<BusinessUnitReply> patchBusinessUnitEntity(PatchBusinessUnitRequest request, String id) {
        return getBusinessUnitEntityRef(id).<BusinessUnitReply>ask(replyTo -> BusinessUnitRequestToCommand.convert(request, id, replyTo), askTimeout);
    }
    private CompletionStage<BusinessUnitReply> deleteBusinessUnitEntity(String id) {
        return getBusinessUnitEntityRef(id).<BusinessUnitReply>ask(replyTo -> BusinessUnitRequestToCommand.convert(id, replyTo), askTimeout);
    }

    @Override
    public CompletionStage<GetBusinessUnitResponse> getBusinessUnit(String id) {
        return getBusinessUnitEntity(id)
                .thenApply(reply -> {
                    return BusinessUnitReplyToGetResponse.convert(reply);
                });
    }

    @Override
    public CompletionStage<GetBusinessUnitResponse> createBusinessUnit(CreateBusinessUnitRequest request) {
        return createBusinessUnitEntity(request, request.id())
                .thenApply(reply -> {
                    return BusinessUnitReplyToGetResponse.convert(reply);
                })
                .thenCompose(r -> {
                    if(r.getStatusCode()!=null && r.getStatusCode().equals(200)){
                        return getBusinessUnitEntity(request.id())
                                .thenApply(reply -> {
                                    return BusinessUnitReplyToGetResponse.convert(reply);
                                });
                    }
                    return CompletableFuture.completedFuture(r);
                });
    }
    @Override
    public CompletionStage<GetBusinessUnitResponse> updateBusinessUnit(UpdateBusinessUnitRequest request){
        return updateBusinessUnitEntity(request, request.id())
                .thenApply(reply -> {
                    return BusinessUnitReplyToGetResponse.convert(reply);
                })
                .thenCompose(r -> {
                    if(r.getStatusCode()!=null && r.getStatusCode().equals(200)){
                        return getBusinessUnitEntity(request.id())
                                .thenApply(reply -> {
                                    return BusinessUnitReplyToGetResponse.convert(reply);
                                });
                    }
                    return CompletableFuture.completedFuture(r);
                });
    }
    @Override
    public CompletionStage<GetBusinessUnitResponse> patchBusinessUnit(PatchBusinessUnitRequest request, String id){
        return patchBusinessUnitEntity(request, id)
                .thenApply(reply -> {
                    return BusinessUnitReplyToGetResponse.convert(reply);
                })
                .thenCompose(r -> {
                    if(r.getStatusCode()!=null && r.getStatusCode().equals(200)){
                        return getBusinessUnitEntity(id)
                                .thenApply(reply -> {
                                    return BusinessUnitReplyToGetResponse.convert(reply);
                                });
                    }
                    return CompletableFuture.completedFuture(r);
                });
    }
    @Override
    public CompletionStage<GetBusinessUnitResponse> deleteBusinessUnit(String id) {
        return deleteBusinessUnitEntity(id)
                .thenApply(reply -> {
                    return BusinessUnitReplyToGetResponse.convert(reply);
                });
    }
}
