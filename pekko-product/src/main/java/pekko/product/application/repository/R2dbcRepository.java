package pekko.product.application.repository;

import io.r2dbc.spi.Connection;
import org.apache.pekko.actor.typed.ActorSystem;
import org.apache.pekko.persistence.r2dbc.ConnectionFactoryProvider;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class R2dbcRepository {

    public final static String PEKKO_PROJECTION_R2DBC_CONNECTION_FACTORY = "pekko.projection.r2dbc.connection-factory";
    public final static String PEKKO_PERSISTENCE_R2DBC_CONNECTION_FACTORY = "pekko.persistence.r2dbc.connection-factory";

    public static <T> CompletionStage<T> withPersistenceSession(ActorSystem typedActorSystem, Function<org.apache.pekko.projection.r2dbc.javadsl.R2dbcSession, CompletionStage<T>> stageFunction) {
        return withSession(typedActorSystem,
                PEKKO_PERSISTENCE_R2DBC_CONNECTION_FACTORY,
                stageFunction);
    }
    public static <T> CompletionStage<T> withProjectionSession(ActorSystem typedActorSystem, Function<org.apache.pekko.projection.r2dbc.javadsl.R2dbcSession, CompletionStage<T>> stageFunction) {
        return withSession(typedActorSystem,
                PEKKO_PROJECTION_R2DBC_CONNECTION_FACTORY,
                stageFunction);
    }

    public static <T> CompletionStage<T> withSession(ActorSystem typedActorSystem, String connectionFactory, Function<org.apache.pekko.projection.r2dbc.javadsl.R2dbcSession, CompletionStage<T>> stageFunction) {
        return Mono.usingWhen(
                        ConnectionFactoryProvider.get(typedActorSystem).connectionFactoryFor(connectionFactory).create(),
                        connection -> Mono.fromCompletionStage(stageFunction.apply(getR2dbcSession(typedActorSystem, connection))),
                        connection -> connection.close())
                .toFuture();
    }

    private static org.apache.pekko.projection.r2dbc.javadsl.R2dbcSession getR2dbcSession(ActorSystem typedActorSystem, Connection connection){
        return new org.apache.pekko.projection.r2dbc.javadsl.R2dbcSession(connection, typedActorSystem.executionContext(), typedActorSystem);
    }
}
