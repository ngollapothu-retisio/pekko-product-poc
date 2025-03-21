package pekko.product;

import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.typed.javadsl.Adapter;
import org.apache.pekko.management.cluster.bootstrap.ClusterBootstrap;
import org.apache.pekko.management.javadsl.PekkoManagement;
import org.apache.pekko.persistence.r2dbc.internal.R2dbcExecutor;
import org.apache.pekko.persistence.r2dbc.state.R2dbcDurableStateStoreProvider;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class PekkoClusterSetup {

    @Inject
    public PekkoClusterSetup(ActorSystem system){

        log.info("PekkoClusterSetup");
        org.apache.pekko.actor.typed.ActorSystem<Void> typedActorSystem = Adapter.toTyped(system);

        PekkoManagement.get(typedActorSystem).start();
        ClusterBootstrap.get(typedActorSystem).start();
    }

}
