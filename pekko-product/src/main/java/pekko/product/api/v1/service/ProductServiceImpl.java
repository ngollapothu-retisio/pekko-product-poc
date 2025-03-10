package pekko.product.api.v1.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.typed.javadsl.Adapter;
import org.apache.pekko.cluster.sharding.typed.javadsl.ClusterSharding;
import org.apache.pekko.cluster.sharding.typed.javadsl.EntityRef;
import pekko.product.api.v1.converter.ProductReplyToGetResponse;
import pekko.product.api.v1.converter.ProductRequestToCommand;
import pekko.product.api.v1.request.CreateProductRequest;
import pekko.product.api.v1.request.PatchProductRequest;
import pekko.product.api.v1.request.UpdateProductRequest;
import pekko.product.api.v1.response.GetProductResponse;
import pekko.product.application.command.ProductCommand;
import pekko.product.application.entity.ProductEntity;
import pekko.product.application.event.ProductEvent;
import pekko.product.application.handler.EventSourcedProjection;
import pekko.product.application.handler.ProductEventHandler;
import pekko.product.application.reply.ProductReply;

import javax.inject.Inject;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ClusterSharding clusterSharding;

    @Inject
    public ProductServiceImpl(ActorSystem classicActorSystem, ProductEventHandler productEventHandler){
        org.apache.pekko.actor.typed.ActorSystem<Void> typedActorSystem = Adapter.toTyped(classicActorSystem);
        this.clusterSharding = ClusterSharding.get(typedActorSystem);

        ProductEntity.init(clusterSharding,10,30);

        EventSourcedProjection.<ProductEvent>init(4, typedActorSystem, ProductEntity.ENTITY_TYPE_KEY.name(), "Products", "products-", productEventHandler);

        log.info("pekko actors registration is processed");
    }

    private static final Duration askTimeout = Duration.ofSeconds(10);

    public EntityRef<ProductCommand> getProductEntityRef(String entityId) {
        return clusterSharding.entityRefFor(ProductEntity.ENTITY_TYPE_KEY, entityId);
    }

    public CompletionStage<ProductReply> getProductEntity(String id) {
        return getProductEntityRef(id).<ProductReply>ask(replyTo -> new ProductCommand.Get(id, replyTo), askTimeout);
    }
    private CompletionStage<ProductReply> createProductEntity(CreateProductRequest request, String id) {
        return getProductEntityRef(id).<ProductReply>ask(replyTo -> ProductRequestToCommand.convert(request, replyTo), askTimeout);
    }
    private CompletionStage<ProductReply> updateProductEntity(UpdateProductRequest request, String id) {
        return getProductEntityRef(id).<ProductReply>ask(replyTo -> ProductRequestToCommand.convert(request, replyTo), askTimeout);
    }
    private CompletionStage<ProductReply> patchProductEntity(PatchProductRequest request, String id) {
        return getProductEntityRef(id).<ProductReply>ask(replyTo -> ProductRequestToCommand.convert(request, id, replyTo), askTimeout);
    }
    private CompletionStage<ProductReply> deleteProductEntity(String id) {
        return getProductEntityRef(id).<ProductReply>ask(replyTo -> ProductRequestToCommand.convert(id, replyTo), askTimeout);
    }

    @Override
    public CompletionStage<GetProductResponse> getProduct(String id) {
        return getProductEntity(id)
                .thenApply(reply -> {
                    return ProductReplyToGetResponse.convert(reply);
                });
    }

    @Override
    public CompletionStage<GetProductResponse> createProduct(CreateProductRequest request) {
        return createProductEntity(request, request.id())
                .thenApply(reply -> {
                    return ProductReplyToGetResponse.convert(reply);
                })
                .thenCompose(r -> {
                    if(r.getStatusCode()!=null && r.getStatusCode().equals(200)){
                        return getProductEntity(request.id())
                                .thenApply(reply -> {
                                    return ProductReplyToGetResponse.convert(reply);
                                });
                    }
                    return CompletableFuture.completedFuture(r);
                });
    }
    @Override
    public CompletionStage<GetProductResponse> updateProduct(UpdateProductRequest request){
        return updateProductEntity(request, request.id())
                .thenApply(reply -> {
                    return ProductReplyToGetResponse.convert(reply);
                })
                .thenCompose(r -> {
                    if(r.getStatusCode()!=null && r.getStatusCode().equals(200)){
                        return getProductEntity(request.id())
                                .thenApply(reply -> {
                                    return ProductReplyToGetResponse.convert(reply);
                                });
                    }
                    return CompletableFuture.completedFuture(r);
                });
    }
    @Override
    public CompletionStage<GetProductResponse> patchProduct(PatchProductRequest request, String id){
        return patchProductEntity(request, id)
                .thenApply(reply -> {
                    return ProductReplyToGetResponse.convert(reply);
                })
                .thenCompose(r -> {
                    if(r.getStatusCode()!=null && r.getStatusCode().equals(200)){
                        return getProductEntity(id)
                                .thenApply(reply -> {
                                    return ProductReplyToGetResponse.convert(reply);
                                });
                    }
                    return CompletableFuture.completedFuture(r);
                });
    }
    @Override
    public CompletionStage<GetProductResponse> deleteProduct(String id) {
        return deleteProductEntity(id)
                .thenApply(reply -> {
                    return ProductReplyToGetResponse.convert(reply);
                });
    }
}
