package pekko.product.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.Done;
import org.apache.pekko.persistence.query.typed.EventEnvelope;
import org.apache.pekko.projection.r2dbc.javadsl.R2dbcHandler;
import org.apache.pekko.projection.r2dbc.javadsl.R2dbcSession;
import pekko.product.application.event.ProductEvent;
import pekko.product.application.util.ObjectMapperUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Slf4j
public class ProductEventHandler extends R2dbcHandler<EventEnvelope<ProductEvent>> {
    @Override
    public CompletionStage<Done> process(R2dbcSession session, EventEnvelope<ProductEvent> envelope) {
        ProductEvent event = envelope.event();

        if(event instanceof ProductEvent.Created){
            log.info("Created, event::{}", ObjectMapperUtil.toJsonString(event));
        } else if(event instanceof ProductEvent.Updated){
            log.info("Updated, event::{}", ObjectMapperUtil.toJsonString(event));
        } else if(event instanceof ProductEvent.Patched){
            log.info("Patched, event::{}", ObjectMapperUtil.toJsonString(event));
        } else if(event instanceof ProductEvent.Deleted){
            log.info("Deleted, event::{}", ObjectMapperUtil.toJsonString(event));
        }
        return CompletableFuture.completedFuture(Done.done());
    }
}
