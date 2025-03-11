package pekko.product.application.converter;

import pekko.product.application.domain.BusinessUnit;
import pekko.product.application.event.publish.BusinessUnitPublishEvent;

public interface BusinessUnitToPublishEvent {
    public static BusinessUnitPublishEvent.BusinessUnitCreated toCreatedEvent(String id, BusinessUnit businessUnit){
        return new BusinessUnitPublishEvent.BusinessUnitCreated(
                id,
                new BusinessUnitPublishEvent.BusinessUnitCreated.BusinessUnit(
                        businessUnit.id(),
                        businessUnit.name(),
                        businessUnit.active(),
                        businessUnit.deleted()
                )
        );
    }
    public static BusinessUnitPublishEvent.BusinessUnitUpdated toUpdatedEvent(String id, BusinessUnit businessUnit){
        return new BusinessUnitPublishEvent.BusinessUnitUpdated(
                id,
                new BusinessUnitPublishEvent.BusinessUnitUpdated.BusinessUnit(
                        businessUnit.id(),
                        businessUnit.name(),
                        businessUnit.active(),
                        businessUnit.deleted()
                )
        );
    }
    public static BusinessUnitPublishEvent.BusinessUnitPatched toPatchedEvent(String id, BusinessUnit businessUnit){
        return new BusinessUnitPublishEvent.BusinessUnitPatched(
                id,
                new BusinessUnitPublishEvent.BusinessUnitPatched.BusinessUnit(
                        businessUnit.id(),
                        businessUnit.name(),
                        businessUnit.active(),
                        businessUnit.deleted()
                )
        );
    }
    public static BusinessUnitPublishEvent.BusinessUnitDeleted toDeletedEvent(String id){
        return new BusinessUnitPublishEvent.BusinessUnitDeleted(
                id
        );
    }
}
