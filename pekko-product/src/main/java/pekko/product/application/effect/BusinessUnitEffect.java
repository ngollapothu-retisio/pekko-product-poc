package pekko.product.application.effect;

import lombok.extern.slf4j.Slf4j;
import org.apache.pekko.persistence.typed.state.javadsl.ReplyEffect;
import pekko.product.application.command.BusinessUnitCommand;
import pekko.product.application.converter.BusinessUnitCommandToState;
import pekko.product.application.entity.BusinessUnitEntity;
import pekko.product.application.reply.BusinessUnitReply;
import pekko.product.application.state.BusinessUnitState;

import static pekko.product.application.constant.ProductConstant.INVALID;
import static pekko.product.application.constant.ProductConstant.SUCCESS;
@Slf4j
public final class BusinessUnitEffect {
    public static ReplyEffect<BusinessUnitState> onGet(BusinessUnitEntity entity, BusinessUnitState state, BusinessUnitCommand.Get command) {
        if (state.businessUnit.isPresent()) {
            log.info("BusinessUnit is fetched. id::{}", command.id());
            return entity.Effect().reply(command.replyTo(), new BusinessUnitReply.Domain(state.businessUnit.get()));
        } else {
            log.info("BusinessUnit does not exist. id::{}", command.id());
            return entity.Effect().reply(command.replyTo(), new BusinessUnitReply.InvalidCommand(INVALID, "BusinessUnit does not exist"));
        }
    }
    public static ReplyEffect<BusinessUnitState> onCreate(BusinessUnitEntity entity, BusinessUnitState state, BusinessUnitCommand.Create command) {
        if (state.businessUnit.isPresent()) {
            log.info("BusinessUnit already exists. id::{}", command.id());
            return entity.Effect().reply(command.replyTo(), new BusinessUnitReply.InvalidCommand(INVALID, "BusinessUnit already exists"));
        } else {
            log.info("BusinessUnit is created. id::{}", command.id());
            return entity.Effect()
                    .persist(BusinessUnitCommandToState.convert(command)) // Tag the state change
                    .thenReply(command.replyTo(), updatedState -> new BusinessUnitReply.SuccessCommand(SUCCESS, "BusinessUnit is created"));
        }
    }
    public static ReplyEffect<BusinessUnitState> onUpdate(BusinessUnitEntity entity, BusinessUnitState state, BusinessUnitCommand.Update command) {
        if (state.businessUnit.isPresent()) {
            log.info("BusinessUnit is updated. id::{}", command.id());
            return entity.Effect()
                    .persist(BusinessUnitCommandToState.convert(command)) // Tag the state change
                    .thenReply(command.replyTo(), updatedState -> new BusinessUnitReply.SuccessCommand(SUCCESS, "BusinessUnit is updated"));
        } else {
            log.info("BusinessUnit does not exist. id::{}", command.id());
            return entity.Effect().reply(command.replyTo(), new BusinessUnitReply.InvalidCommand(INVALID, "BusinessUnit does not exist"));
        }
    }
    public static ReplyEffect<BusinessUnitState> onPatch(BusinessUnitEntity entity, BusinessUnitState state, BusinessUnitCommand.Patch command) {
        if (state.businessUnit.isPresent()) {
            log.info("BusinessUnit is patched. id::{}", command.id());
            return entity.Effect()
                    .persist(BusinessUnitCommandToState.convert(state, command)) // Tag the state change
                    .thenReply(command.replyTo(), updatedState -> new BusinessUnitReply.SuccessCommand(SUCCESS, "BusinessUnit is patched"));
        } else {
            log.info("BusinessUnit does not exist. id::{}", command.id());
            return entity.Effect().reply(command.replyTo(), new BusinessUnitReply.InvalidCommand(INVALID, "BusinessUnit does not exist"));
        }
    }
    public static ReplyEffect<BusinessUnitState> onDelete(BusinessUnitEntity entity, BusinessUnitState state, BusinessUnitCommand.Delete command) {
        if (state.businessUnit.isPresent()) {
            log.info("BusinessUnit is deleted. id::{}", command.id());
            return entity.Effect()
                    .persist(BusinessUnitCommandToState.convert(command))
                    .thenReply(command.replyTo(), updatedState -> new BusinessUnitReply.SuccessCommand(SUCCESS, "BusinessUnit is deleted"));
        } else {
            log.info("BusinessUnit does not exist. id::{}", command.id());
            return entity.Effect().reply(command.replyTo(), new BusinessUnitReply.InvalidCommand(INVALID, "BusinessUnit does not exist"));
        }
    }

}
