package pekko.product.application.state;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.EqualsAndHashCode;
import lombok.Value;
import pekko.product.application.domain.BusinessUnit;
import pekko.product.application.serializer.JsonSerializable;

import java.util.Optional;

@JsonDeserialize
public class BusinessUnitState implements JsonSerializable {

    public static final BusinessUnitState EMPTY = new BusinessUnitState(Optional.empty());

    public final Optional<BusinessUnit> businessUnit;

    @JsonCreator
    public BusinessUnitState(Optional<BusinessUnit> businessUnit){
        this.businessUnit = businessUnit;
    }

    @Value
    @JsonDeserialize
    @EqualsAndHashCode(callSuper=false)
    public static class Created extends BusinessUnitState {
        @JsonCreator
        public Created(Optional<BusinessUnit> businessUnit){
            super(businessUnit);
        }
    }
    @Value
    @JsonDeserialize
    @EqualsAndHashCode(callSuper=false)
    public static class Updated extends BusinessUnitState {
        @JsonCreator
        public Updated(Optional<BusinessUnit> businessUnit){
            super(businessUnit);
        }
    }
    @Value
    @JsonDeserialize
    @EqualsAndHashCode(callSuper=false)
    public static class Patched extends BusinessUnitState {
        @JsonCreator
        public Patched(Optional<BusinessUnit> businessUnit){
            super(businessUnit);
        }
    }
    @Value
    @JsonDeserialize
    @EqualsAndHashCode(callSuper=false)
    public static class Deleted extends BusinessUnitState {
        @JsonCreator
        public Deleted(){
            super(Optional.empty());
        }
    }
}
