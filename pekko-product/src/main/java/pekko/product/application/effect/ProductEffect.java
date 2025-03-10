package pekko.product.application.effect;

import lombok.extern.slf4j.Slf4j;
import pekko.product.application.command.ProductCommand;
import org.apache.pekko.persistence.typed.javadsl.ReplyEffect;
import pekko.product.application.entity.ProductEntity;
import pekko.product.application.event.ProductEvent;
import pekko.product.application.state.ProductState;
import pekko.product.application.reply.ProductReply;

import static pekko.product.application.constant.ProductConstant.INVALID;
import static pekko.product.application.constant.ProductConstant.SUCCESS;
@Slf4j
public final class ProductEffect {

    public static ReplyEffect<ProductEvent, ProductState> onCreate(ProductEntity entity, ProductState state, ProductCommand.Create command) {
        if (state.product().isPresent()) {
            log.info("Product already exists. id::{}", command.id());
            return entity.Effect()
                    .none()
                    .thenReply(command.replyTo(), __ -> new ProductReply.InvalidCommand(INVALID, "Product already exists"));
        } else {
            log.info("Product is created. id::{}", command.id());
            ProductEvent.Created event = new ProductEvent.Created(command.id(), command.name(), command.active());
            return entity.Effect()
                    .persist(event)
                    .thenReply(command.replyTo(), __ -> new ProductReply.SuccessCommand(SUCCESS, "Product is created"));
        }
    }
    public static ReplyEffect<ProductEvent, ProductState> onUpdate(ProductEntity entity, ProductState state, ProductCommand.Update command) {
        if (state.product().isPresent()) {
            log.info("Product is updated. id::{}", command.id());
            ProductEvent.Updated event = new ProductEvent.Updated(command.id(), command.name(), command.active());
            return entity.Effect()
                    .persist(event)
                    .thenReply(command.replyTo(), __ -> new ProductReply.SuccessCommand(SUCCESS, "Product is updated"));
        } else {
            log.info("Product does not exist. id::{}", command.id());
            return entity.Effect()
                    .none()
                    .thenReply(command.replyTo(), __ -> new ProductReply.InvalidCommand(INVALID, "Product does not exist"));
        }
    }
    public static ReplyEffect<ProductEvent, ProductState> onPatch(ProductEntity entity, ProductState state, ProductCommand.Patch command) {
        if (state.product().isPresent()) {
            log.info("Product is patched. id::{}", command.id());
            ProductEvent.Patched event = new ProductEvent.Patched(command.id(), command.name(), command.active());
            return entity.Effect()
                    .persist(event)
                    .thenReply(command.replyTo(), __ -> new ProductReply.SuccessCommand(SUCCESS, "Product is patched"));
        } else {
            log.info("Product does not exist. id::{}", command.id());
            return entity.Effect()
                    .none()
                    .thenReply(command.replyTo(), __ -> new ProductReply.InvalidCommand(INVALID, "Product does not exist"));
        }
    }
    public static ReplyEffect<ProductEvent, ProductState> onDelete(ProductEntity entity, ProductState state, ProductCommand.Delete command) {
        if (state.product().isPresent()) {
            log.info("Product is deleted. id::{}", command.id());
            ProductEvent.Deleted event = new ProductEvent.Deleted(command.id());
            return entity.Effect()
                    .persist(event)
                    .thenReply(command.replyTo(), __ -> new ProductReply.SuccessCommand(SUCCESS, "Product is deleted"));
        } else {
            log.info("Product does not exist. id::{}", command.id());
            return entity.Effect()
                    .none()
                    .thenReply(command.replyTo(), __ -> new ProductReply.InvalidCommand(INVALID, "Product does not exist"));
        }
    }

    public static ReplyEffect<ProductEvent, ProductState> onGet(ProductEntity entity, ProductState state, ProductCommand.Get command) {
        if (state.product().isPresent()) {
            log.info("Product is found. id::{}", command.id());
            return entity.Effect()
                    .none()
                    .thenReply(command.replyTo(), __ -> new ProductReply.Domain(state.product().get()));
        } else {
            log.info("Product does not exist. id::{}", command.id());
            return entity.Effect()
                    .none()
                    .thenReply(command.replyTo(), __ -> new ProductReply.InvalidCommand(INVALID, "Product does not exist"));
        }

    }
}
