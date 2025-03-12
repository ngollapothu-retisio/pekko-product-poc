package pekko.product.application.handler;

import io.r2dbc.spi.Row;
import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.Done;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.persistence.query.typed.EventEnvelope;
import org.apache.pekko.projection.r2dbc.javadsl.R2dbcHandler;
import org.apache.pekko.projection.r2dbc.javadsl.R2dbcSession;
import pekko.product.application.converter.ProductEventToDomain;
import pekko.product.application.domain.BusinessUnit;
import pekko.product.application.event.ProductEvent;
import pekko.product.application.repository.ProductRepository;
import pekko.product.application.util.KafkaUtil;
import pekko.product.application.util.ObjectMapperUtil;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import static pekko.product.application.util.ObjectMapperUtil.toJsonString;

@Slf4j
public class ProductEventHandler extends R2dbcHandler<EventEnvelope<ProductEvent>> {

    private final KafkaUtil kafkaUtil;
    private final static String TOPIC_NAME = "product-events";

    private final ProductRepository productRepository;

    @Inject
    public ProductEventHandler(ActorSystem classicActorSystem, ProductRepository productRepository){
        this.kafkaUtil = new KafkaUtil(classicActorSystem);
        this.productRepository = productRepository;
    }

    @Override
    public CompletionStage<Done> process(R2dbcSession session, EventEnvelope<ProductEvent> envelope) {
        ProductEvent event = envelope.event();

        if(event instanceof ProductEvent.Created){
            ProductEvent.Created created = (ProductEvent.Created) event;
            log.info("Created, event::{}", toJsonString(created));
            return kafkaUtil.send(TOPIC_NAME, created.id(), toJsonString(created))
                    .thenCompose(done -> productRepository.saveProduct(ProductEventToDomain.convert(created)));
        } else if(event instanceof ProductEvent.Updated){
            ProductEvent.Updated updated = (ProductEvent.Updated) event;
            log.info("Updated, event::{}", toJsonString(updated));
            return kafkaUtil.send(TOPIC_NAME, updated.id(), toJsonString(updated))
                    .thenCompose(done -> productRepository.saveProduct(ProductEventToDomain.convert(updated)));
        } else if(event instanceof ProductEvent.Patched){
            ProductEvent.Patched patched = (ProductEvent.Patched) event;
            log.info("Patched, event::{}", toJsonString(patched));
            return kafkaUtil.send(TOPIC_NAME, patched.id(), toJsonString(patched))
                    .thenCompose(done -> productRepository.patchProduct(patched, patched.id()));
        } else if(event instanceof ProductEvent.Deleted){
            ProductEvent.Deleted deleted = (ProductEvent.Deleted) event;
            log.info("Deleted, event::{}", toJsonString(deleted));
            return kafkaUtil.send(TOPIC_NAME, deleted.id(), toJsonString(deleted))
                    .thenCompose(done -> productRepository.deleteProduct(deleted.id()));
        } else {
            log.info("Event ignored, class::{}, event::{}", event.getClass(), toJsonString(event));
        }
        return CompletableFuture.completedFuture(Done.done());
    }


}
