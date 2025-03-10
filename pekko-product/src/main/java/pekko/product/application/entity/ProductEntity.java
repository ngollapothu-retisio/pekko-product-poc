package pekko.product.application.entity;

import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.actor.typed.ActorSystem;
import org.apache.pekko.actor.typed.Behavior;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.apache.pekko.cluster.sharding.typed.javadsl.ClusterSharding;
import org.apache.pekko.cluster.sharding.typed.javadsl.Entity;
import org.apache.pekko.cluster.sharding.typed.javadsl.EntityTypeKey;
import org.apache.pekko.persistence.typed.PersistenceId;
import org.apache.pekko.persistence.typed.javadsl.*;
import pekko.product.application.command.ProductCommand;
import pekko.product.application.converter.ProductEventToState;
import pekko.product.application.effect.ProductEffect;
import pekko.product.application.event.ProductEvent;
import pekko.product.application.state.ProductState;

@Slf4j
public class ProductEntity extends EventSourcedBehaviorWithEnforcedReplies<ProductCommand, ProductEvent, ProductState> {

    //----------------------------
    public static EntityTypeKey<ProductCommand> ENTITY_TYPE_KEY = EntityTypeKey.create(ProductCommand.class, "CatalogEntity");
    private final ActorSystem system;
    static Integer numberOfEvents;
    static Integer keepNSnapshots;

    public static void init(ClusterSharding clusterSharding, Integer numberOfEvents, Integer keepNSnapshots) {
        clusterSharding.init(
                Entity.of(
                        ENTITY_TYPE_KEY,
                        entityContext -> {
                            return ProductEntity.create(entityContext.getEntityId(), numberOfEvents, keepNSnapshots);
                        }));
        log.info("{} init is completed ....", ENTITY_TYPE_KEY.name());
    }

    public static Behavior<ProductCommand> create(String entityId, Integer numberOfEvents, Integer keepNSnapshots) {
        return Behaviors.setup(
                ctx -> EventSourcedBehavior.start(new ProductEntity(ctx.getSystem(), entityId, numberOfEvents, keepNSnapshots), ctx));
    }

    private ProductEntity(ActorSystem system, String entityId, Integer numberOfEvents, Integer keepNSnapshots) {
        super(
                PersistenceId.of(ENTITY_TYPE_KEY.name(), entityId)
        );
        this.system = system;
        this.numberOfEvents = numberOfEvents;
        this.keepNSnapshots = keepNSnapshots;
    }
    //----------------------------------
    @Override
    public RetentionCriteria retentionCriteria() {
        return RetentionCriteria.snapshotEvery(numberOfEvents,
                keepNSnapshots).withDeleteEventsOnSnapshot();
    }
    //---------------------------------

    @Override
    public ProductState emptyState() {
        return ProductState.EMPTY;
    }

    @Override
    public CommandHandlerWithReply<ProductCommand, ProductEvent, ProductState> commandHandler() {
        return newCommandHandlerWithReplyBuilder()
                .forAnyState()
                .onCommand(ProductCommand.Get.class, (state, cmd) -> ProductEffect.onGet(this, state, cmd))
                .onCommand(ProductCommand.Create.class, (state, cmd) ->  ProductEffect.onCreate(this, state, cmd))
                .onCommand(ProductCommand.Update.class, (state, cmd) ->  ProductEffect.onUpdate(this, state, cmd))
                .onCommand(ProductCommand.Patch.class, (state, cmd) ->  ProductEffect.onPatch(this, state, cmd))
                .onCommand(ProductCommand.Delete.class, (state, cmd) ->  ProductEffect.onDelete(this, state, cmd))
                .build();
    }

    @Override
    public EventHandler<ProductState, ProductEvent> eventHandler() {
        return newEventHandlerBuilder().
                forAnyState()
                .onEvent(ProductEvent.Created.class, (state, evt) -> ProductEventToState.convert(evt))
                .onEvent(ProductEvent.Updated.class, (state, evt) -> ProductEventToState.convert(evt))
                .onEvent(ProductEvent.Patched.class, (state, evt) -> ProductEventToState.convert(state, evt))
                .onEvent(ProductEvent.Deleted.class, (state, evt) -> ProductEventToState.convert(evt))
                .build();
    }
    //-------------------------------

}
