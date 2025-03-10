package pekko.product.application.handler;


import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.actor.typed.ActorSystem;
import org.apache.pekko.cluster.sharding.typed.javadsl.ShardedDaemonProcess;
import org.apache.pekko.japi.Pair;
import org.apache.pekko.persistence.query.DurableStateChange;
import org.apache.pekko.persistence.query.Offset;
import org.apache.pekko.persistence.r2dbc.state.javadsl.R2dbcDurableStateStore;
import org.apache.pekko.projection.Projection;
import org.apache.pekko.projection.ProjectionBehavior;
import org.apache.pekko.projection.ProjectionId;
import org.apache.pekko.projection.javadsl.SourceProvider;
import org.apache.pekko.projection.r2dbc.R2dbcProjectionSettings;
import org.apache.pekko.projection.r2dbc.javadsl.R2dbcHandler;
import org.apache.pekko.projection.r2dbc.javadsl.R2dbcProjection;
import org.apache.pekko.projection.state.javadsl.DurableStateSourceProvider;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
public class DurableStateProjection<T> {

    public static<T> void init(int numberOfSliceRanges,
                               ActorSystem system,
                               String entityType,
                               String name,
                               String key,
                               R2dbcHandler<DurableStateChange<T>> handler) {

        List<Pair<Integer, Integer>> sliceRanges =
                DurableStateSourceProvider.sliceRanges(
                        system, R2dbcDurableStateStore.Identifier(), numberOfSliceRanges);

        ShardedDaemonProcess.get(system)
                .init(
                        ProjectionBehavior.Command.class,
                        name,
                        sliceRanges.size(),
                        i -> ProjectionBehavior.create(createProjection(system, entityType, name, key, sliceRanges.get(i), handler)),
                        ProjectionBehavior.stopMessage());
    }

    private static<T> Projection<DurableStateChange<T>> createProjection(
            ActorSystem<?> system,
            String entityType,
            String name,
            String key,
            Pair<Integer, Integer> sliceRange,
            R2dbcHandler<DurableStateChange<T>> handler) {
        int minSlice = sliceRange.first();
        int maxSlice = sliceRange.second();

        SourceProvider<Offset, DurableStateChange<T>> sourceProvider =
                DurableStateSourceProvider.changesBySlices(
                        system, R2dbcDurableStateStore.Identifier(), entityType, minSlice, maxSlice);

        ProjectionId projectionId =
                ProjectionId.of(name, key + minSlice + "-" + maxSlice);
        Optional<R2dbcProjectionSettings> settings = Optional.empty();

        int saveOffsetAfterEnvelopes = 100;
        Duration saveOffsetAfterDuration = Duration.ofMillis(500l);
        log.info("{} init()..................", name);
        return R2dbcProjection.atLeastOnce(
                        projectionId, settings, sourceProvider, () -> handler, system)
                .withSaveOffset(saveOffsetAfterEnvelopes, saveOffsetAfterDuration);
    }
}