package pekko.product.application.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.pekko.Done;
import org.apache.pekko.projection.r2dbc.javadsl.R2dbcHandler;
import org.apache.pekko.projection.r2dbc.javadsl.R2dbcSession;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Slf4j
public class BrandMessageHandler extends R2dbcHandler<ConsumerRecord<String, String>> {

    @Override
    public CompletionStage<Done> process(R2dbcSession session, ConsumerRecord<String, String> record) {
        String topic = record.topic();
        String key = record.key();
        String value = record.value();
        log.info("Brand Message ,session::{}, topic::{}, key::{}, value::{}", session, topic, key, value);
        return CompletableFuture.completedFuture(Done.done());
    }
}
