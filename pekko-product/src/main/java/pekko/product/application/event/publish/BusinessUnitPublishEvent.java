package pekko.product.application.event.publish;

import com.fasterxml.jackson.annotation.*;
import pekko.product.application.serializer.JsonSerializable;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Void.class)
@JsonSubTypes({
        @JsonSubTypes.Type(BusinessUnitPublishEvent.BusinessUnitCreated.class),
        @JsonSubTypes.Type(BusinessUnitPublishEvent.BusinessUnitUpdated.class),
        @JsonSubTypes.Type(BusinessUnitPublishEvent.BusinessUnitPatched.class),
        @JsonSubTypes.Type(BusinessUnitPublishEvent.BusinessUnitDeleted.class)
})
public interface BusinessUnitPublishEvent {

    String id();

    @JsonTypeName(value = "business-unit-created")
    record BusinessUnitCreated(String id, BusinessUnit businessUnit) implements BusinessUnitPublishEvent {
        @Override
        public String id() {
            return this.id;
        }
    }

    @JsonTypeName(value = "business-unit-updated")
    record BusinessUnitUpdated(String id, BusinessUnit businessUnit) implements BusinessUnitPublishEvent {
        @Override
        public String id() {
            return this.id;
        }
    }

    @JsonTypeName(value = "business-unit-patched")
    record BusinessUnitPatched(String id, BusinessUnit businessUnit) implements BusinessUnitPublishEvent {
        @Override
        public String id() {
            return this.id;
        }
    }

    @JsonTypeName(value = "business-unit-deleted")
    record BusinessUnitDeleted(String id) implements BusinessUnitPublishEvent {
        @Override
        public String id() {
            return this.id;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    record BusinessUnit(String id, String name, Boolean active, Boolean deleted) implements JsonSerializable {
    }

}