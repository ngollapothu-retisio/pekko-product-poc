package pekko.product.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.Done;
import org.apache.pekko.persistence.query.DurableStateChange;
import org.apache.pekko.persistence.query.UpdatedDurableState;
import org.apache.pekko.persistence.query.typed.EventEnvelope;
import org.apache.pekko.projection.r2dbc.javadsl.R2dbcHandler;
import org.apache.pekko.projection.r2dbc.javadsl.R2dbcSession;
import pekko.product.application.domain.BusinessUnit;
import pekko.product.application.entity.BusinessUnitEntity;
import pekko.product.application.event.ProductEvent;
import pekko.product.application.state.BusinessUnitState;
import pekko.product.application.util.ObjectMapperUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Slf4j
public class BusinessUnitStateChangeHandler extends R2dbcHandler<DurableStateChange<BusinessUnitState>> {
    @Override
    public CompletionStage<Done> process(R2dbcSession session, DurableStateChange<BusinessUnitState> change) {
        String persistenceId = change.persistenceId();
        String entityName = BusinessUnitEntity.ENTITY_TYPE_KEY.name();
        String entityId = persistenceId.substring((entityName+"|").length());
        if(change instanceof UpdatedDurableState) {
            log.info("change found for persistenceId::{}", persistenceId);
            UpdatedDurableState<BusinessUnitState> updated = (UpdatedDurableState<BusinessUnitState>) change;
            BusinessUnitState businessUnitState = updated.value();
            log.info("change found for businessUnit::{}", businessUnitState.businessUnit);
            if (businessUnitState.businessUnit.isPresent()) {
                BusinessUnit businessUnit = businessUnitState.businessUnit.get();
                log.info("businessUnit::{}", businessUnit);
                if (businessUnitState instanceof BusinessUnitState.Created) {
                    log.info("Created. revision::{}, persistenceId::{}", updated.revision(), persistenceId);
                } else if (businessUnitState instanceof BusinessUnitState.Updated) {
                    log.info("Updated. revision::{}, persistenceId::{}", updated.revision(), persistenceId);
                } else if (businessUnitState instanceof BusinessUnitState.Patched) {
                    log.info("Patched. revision::{}, persistenceId::{}", updated.revision(), persistenceId);
                }
            } else if (businessUnitState instanceof BusinessUnitState.Deleted) {
                log.info("Deleted. revision::{}, persistenceId::{}", updated.revision(), persistenceId);
            }
        }
        log.info("class::{}, persistenceId::{}", change.getClass(), change.persistenceId());
        return CompletableFuture.completedFuture(Done.getInstance());
    }
}
