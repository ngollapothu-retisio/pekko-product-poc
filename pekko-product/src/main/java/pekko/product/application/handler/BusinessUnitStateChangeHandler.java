package pekko.product.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.Done;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.persistence.query.DurableStateChange;
import org.apache.pekko.persistence.query.UpdatedDurableState;
import org.apache.pekko.projection.r2dbc.javadsl.R2dbcHandler;
import org.apache.pekko.projection.r2dbc.javadsl.R2dbcSession;
import pekko.product.application.domain.BusinessUnit;
import pekko.product.application.entity.BusinessUnitEntity;
import pekko.product.application.state.BusinessUnitState;
import pekko.product.application.util.KafkaUtil;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static pekko.product.application.converter.BusinessUnitToPublishEvent.*;
import static pekko.product.application.util.ObjectMapperUtil.toJsonString;

@Slf4j
public class BusinessUnitStateChangeHandler extends R2dbcHandler<DurableStateChange<BusinessUnitState>> {

    private final KafkaUtil kafkaUtil;
    private final static String TOPIC_NAME = "business-unit-events";

    @Inject
    public BusinessUnitStateChangeHandler(ActorSystem classicActorSystem){
        this.kafkaUtil = new KafkaUtil(classicActorSystem);
    }

    @Override
    public CompletionStage<Done> process(R2dbcSession session, DurableStateChange<BusinessUnitState> change) {
        String persistenceId = change.persistenceId();
        String entityName = BusinessUnitEntity.ENTITY_TYPE_KEY.name();
        String entityId = persistenceId.substring((entityName+"|").length());
        if(change instanceof UpdatedDurableState) {
            UpdatedDurableState<BusinessUnitState> updated = (UpdatedDurableState<BusinessUnitState>) change;
            BusinessUnitState businessUnitState = updated.value();
            if (businessUnitState.businessUnit.isPresent()) {
                BusinessUnit businessUnit = businessUnitState.businessUnit.get();
                if (businessUnitState instanceof BusinessUnitState.Created) {
                    log.info("Created. revision::{}, persistenceId::{}", updated.revision(), persistenceId);
                    return kafkaUtil.send(TOPIC_NAME, businessUnit.id(), toJsonString(toCreatedEvent(entityId, businessUnit)));
                } else if (businessUnitState instanceof BusinessUnitState.Updated) {
                    log.info("Updated. revision::{}, persistenceId::{}", updated.revision(), persistenceId);
                    return kafkaUtil.send(TOPIC_NAME, businessUnit.id(), toJsonString(toUpdatedEvent(entityId, businessUnit)));
                } else if (businessUnitState instanceof BusinessUnitState.Patched) {
                    log.info("Patched. revision::{}, persistenceId::{}", updated.revision(), persistenceId);
                    return kafkaUtil.send(TOPIC_NAME, businessUnit.id(), toJsonString(toPatchedEvent(entityId, businessUnit)));
                }
            } else if (businessUnitState instanceof BusinessUnitState.Deleted) {
                log.info("Deleted. revision::{}, persistenceId::{}", updated.revision(), persistenceId);
                return kafkaUtil.send(TOPIC_NAME, entityId, toJsonString(toDeletedEvent(entityId)));
            }
        } else {
            log.info("Change ignored, class::{}, persistenceId::{}", change.getClass(), change.persistenceId());
        }
        return CompletableFuture.completedFuture(Done.getInstance());
    }
}
