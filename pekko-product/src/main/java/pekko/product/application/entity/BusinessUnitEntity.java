package pekko.product.application.entity;

import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.cluster.sharding.typed.javadsl.ClusterSharding;
import org.apache.pekko.cluster.sharding.typed.javadsl.Entity;
import org.apache.pekko.cluster.sharding.typed.javadsl.EntityContext;
import org.apache.pekko.cluster.sharding.typed.javadsl.EntityTypeKey;
import org.apache.pekko.persistence.typed.PersistenceId;
import org.apache.pekko.persistence.typed.state.javadsl.CommandHandlerWithReply;
import org.apache.pekko.persistence.typed.state.javadsl.DurableStateBehaviorWithEnforcedReplies;
import pekko.product.application.command.BusinessUnitCommand;
import pekko.product.application.effect.BusinessUnitEffect;
import pekko.product.application.state.BusinessUnitState;

@Slf4j
public class BusinessUnitEntity extends DurableStateBehaviorWithEnforcedReplies<BusinessUnitCommand, BusinessUnitState> {

    public static final EntityTypeKey<BusinessUnitCommand> ENTITY_TYPE_KEY =
            EntityTypeKey.create(BusinessUnitCommand.class, "BusinessUnitEntity");

    //initializing the entity in clusterSharding
    public static void init(ClusterSharding clusterSharding) {
        clusterSharding.init(
                Entity.of(
                        ENTITY_TYPE_KEY,
                        entityContext -> new BusinessUnitEntity(entityContext)));
    }

    // Constructor
    private BusinessUnitEntity(EntityContext<BusinessUnitCommand> entityContext) {
        super(PersistenceId.of(entityContext.getEntityTypeKey().name(), entityContext.getEntityId()));
    }

    // Initial state
    @Override
    public BusinessUnitState emptyState() {
        return BusinessUnitState.EMPTY;
    }

    // Command handler
    @Override
    public CommandHandlerWithReply<BusinessUnitCommand, BusinessUnitState> commandHandler() {
        return newCommandHandlerWithReplyBuilder()
                .forAnyState()
                .onCommand(BusinessUnitCommand.Create.class, (state, cmd) -> BusinessUnitEffect.onCreate(this, state, cmd))
                .onCommand(BusinessUnitCommand.Update.class, (state, cmd) -> BusinessUnitEffect.onUpdate(this, state, cmd))
                .onCommand(BusinessUnitCommand.Patch.class, (state, cmd) -> BusinessUnitEffect.onPatch(this, state, cmd))
                .onCommand(BusinessUnitCommand.Delete.class, (state, cmd) -> BusinessUnitEffect.onDelete(this, state, cmd))
                .onCommand(BusinessUnitCommand.Get.class, (state, cmd) -> BusinessUnitEffect.onGet(this, state, cmd))
                .build();
    }

}
