package pekko.product;

import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.typed.javadsl.Adapter;
import org.apache.pekko.cluster.sharding.typed.javadsl.ClusterSharding;
import pekko.product.application.entity.BusinessUnitEntity;
import pekko.product.application.entity.ProductEntity;
import pekko.product.application.event.ProductEvent;
import pekko.product.application.handler.BusinessUnitStateChangeHandler;
import pekko.product.application.handler.DurableStateProjection;
import pekko.product.application.handler.EventSourcedProjection;
import pekko.product.application.handler.ProductEventHandler;
import pekko.product.application.kafka.BrandMessageHandler;
import pekko.product.application.kafka.KafkaProjection;
import pekko.product.application.state.BusinessUnitState;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class PersistenceAndProjectionSetup {

    private final ClusterSharding clusterSharding;
    
    @Inject
    public PersistenceAndProjectionSetup(
            ActorSystem classicActorSystem, 
            ProductEventHandler productEventHandler, 
            BusinessUnitStateChangeHandler businessUnitStateChangeHandler, 
            BrandMessageHandler brandMessageHandler
    ){
        org.apache.pekko.actor.typed.ActorSystem<Void> typedActorSystem = Adapter.toTyped(classicActorSystem);
        this.clusterSharding = ClusterSharding.get(typedActorSystem);
        
        //product events persistence and projection setup
        ProductEntity.init(clusterSharding,10,30);
        EventSourcedProjection.<ProductEvent>init(4, typedActorSystem, ProductEntity.ENTITY_TYPE_KEY.name(), "Products", "products-", productEventHandler);

        //business unit state persistence and projection setup
        BusinessUnitEntity.init(clusterSharding);
        DurableStateProjection.<BusinessUnitState>init(4, typedActorSystem, BusinessUnitEntity.ENTITY_TYPE_KEY.name(), "BusinessUnits", "business-units-", businessUnitStateChangeHandler);

        //brand kafka topic projection
        KafkaProjection.init(4, typedActorSystem, "test-brand-group","brand-events","Brands", brandMessageHandler);
    }

}
