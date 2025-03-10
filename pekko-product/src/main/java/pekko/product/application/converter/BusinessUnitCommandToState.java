package pekko.product.application.converter;

import pekko.product.application.command.BusinessUnitCommand;
import pekko.product.application.domain.BusinessUnit;
import pekko.product.application.domain.Product;
import pekko.product.application.event.ProductEvent;
import pekko.product.application.state.BusinessUnitState;
import pekko.product.application.state.ProductState;

import java.util.Optional;

public interface BusinessUnitCommandToState {
    static BusinessUnitState convert(BusinessUnitCommand.Create command){
        return new BusinessUnitState.Created(
                Optional.of(
                        new BusinessUnit(
                                command.id(),
                                command.name(),
                                command.active(),
                                false
                        )
                )
        );
    }
    static BusinessUnitState convert(BusinessUnitCommand.Update command){
        return new BusinessUnitState.Updated(
                Optional.of(
                        new BusinessUnit(
                                command.id(),
                                command.name(),
                                command.active(),
                                false
                        )
                )
        );
    }
    static BusinessUnitState convert(BusinessUnitState state, BusinessUnitCommand.Patch command){
        if(state.businessUnit.isPresent()){
            BusinessUnit businessUnit = state.businessUnit.get();
            return new BusinessUnitState.Patched(
                    Optional.of(
                            new BusinessUnit(
                                    command.id(),
                                    Optional.ofNullable(command.name()).orElseGet(()->businessUnit.name()),
                                    Optional.ofNullable(command.active()).orElseGet(()->businessUnit.active()),
                                    false
                            )
                    )
            );
        }
        return state;
    }
    static BusinessUnitState convert(BusinessUnitCommand.Delete command){
        return new BusinessUnitState.Deleted();
    }
}
