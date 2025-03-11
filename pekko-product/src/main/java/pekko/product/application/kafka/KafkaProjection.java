package pekko.product.application.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.pekko.actor.typed.ActorSystem;
import org.apache.pekko.cluster.sharding.typed.javadsl.ShardedDaemonProcess;
import org.apache.pekko.kafka.ConsumerSettings;
import org.apache.pekko.projection.MergeableOffset;
import org.apache.pekko.projection.Projection;
import org.apache.pekko.projection.ProjectionBehavior;
import org.apache.pekko.projection.ProjectionId;
import org.apache.pekko.projection.javadsl.SourceProvider;
import org.apache.pekko.projection.kafka.javadsl.KafkaSourceProvider;
import org.apache.pekko.projection.r2dbc.R2dbcProjectionSettings;
import org.apache.pekko.projection.r2dbc.javadsl.R2dbcHandler;
import org.apache.pekko.projection.r2dbc.javadsl.R2dbcProjection;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

@Slf4j
public class KafkaProjection {

    public static void init(int numberOfListeners,
                                ActorSystem system,
                                String groupId,
                                String topicName,
                                String name,
                                R2dbcHandler<ConsumerRecord<String, String>> handler) {

        String bootstrapServers = system.settings().config().getString("kafka-connection-settings.bootstrap.servers");
        ConsumerSettings<String, String> consumerSettings =
                ConsumerSettings.create(system, new StringDeserializer(), new StringDeserializer())
                        .withBootstrapServers(bootstrapServers)
                        .withGroupId(groupId)
                        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        ShardedDaemonProcess.get(system)
                .init(
                        ProjectionBehavior.Command.class,
                        name,
                        numberOfListeners,
                        i -> ProjectionBehavior.create(createProjection(system, consumerSettings, topicName, name, i, handler)),
                        ProjectionBehavior.stopMessage());
    }

    private static<T> Projection<ConsumerRecord<String, String>> createProjection(
            ActorSystem<?> system,
            ConsumerSettings<String, String> consumerSettings,
            String topicName,
            String name,
            Integer listenerNumber,
            R2dbcHandler<ConsumerRecord<String, String>> handler) {

        SourceProvider<MergeableOffset<Long>, ConsumerRecord<String, String>> sourceProvider =
                KafkaSourceProvider.create(system, consumerSettings, Collections.singleton(topicName));

        ProjectionId projectionId = ProjectionId.of(name, "listener-"+listenerNumber);
        Optional<R2dbcProjectionSettings> settings = Optional.empty();

        log.info("{} init()..................listenerNumber::{}", name, listenerNumber);
        return R2dbcProjection.exactlyOnce(
                        projectionId, settings, sourceProvider, () -> handler, system);
    }
}
