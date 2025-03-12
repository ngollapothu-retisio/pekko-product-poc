package pekko.product.application.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.pekko.Done;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.kafka.ProducerSettings;
import org.apache.pekko.kafka.javadsl.SendProducer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletionStage;

@Slf4j
@Singleton
public class KafkaUtil  {
    
    private final ProducerSettings<String, String> producerSettings;
    private final SendProducer<String, String> sendProducer;
    
    @Inject
    public KafkaUtil(ActorSystem classicActorSystem) {
        String bootstrapServers = classicActorSystem.settings().config().getString("kafka-connection-settings.bootstrap.servers");
        this.producerSettings =
                ProducerSettings.create(classicActorSystem, new StringSerializer(), new StringSerializer())
                        .withBootstrapServers(bootstrapServers);
        this.sendProducer = new SendProducer<>(producerSettings, classicActorSystem);
    }

    public CompletionStage<Done> send(String topic, String key, String value) {
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, key, value);
        return sendProducer.send(producerRecord)
                .handle(
                        (recordMetadata, exception) -> {
                            if (exception == null) {
                                log.info("Topic::{}, Partition::{}, Offset:{},key::{}, valueSize::{}",
                                        recordMetadata.topic(),
                                        recordMetadata.partition(),
                                        recordMetadata.offset(),
                                        key,
                                        recordMetadata.serializedValueSize());
                            } else {
                                log.error("KafkaError::000 while sending message to Topic::{}, key::{}, value::{}",
                                        topic, key, value, exception);
                                throw new RuntimeException(exception);
                            }
                            return Done.getInstance();
                        }
                );
    }
}
