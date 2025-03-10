package pekko.product.api.v1.converter;

import pekko.product.api.v1.response.GetBusinessUnitResponse;
import pekko.product.application.domain.BusinessUnit;
import pekko.product.application.reply.BusinessUnitReply;

import static pekko.product.application.constant.ProductConstant.INVALID;

public interface BusinessUnitReplyToGetResponse {
    static GetBusinessUnitResponse convert(BusinessUnitReply reply){
        if(reply instanceof BusinessUnitReply.InvalidCommand){
            BusinessUnitReply.InvalidCommand invalidCommand = (BusinessUnitReply.InvalidCommand)reply;
            return new GetBusinessUnitResponse(invalidCommand.code(), invalidCommand.message());
        } else if(reply instanceof BusinessUnitReply.SuccessCommand){
            BusinessUnitReply.SuccessCommand successCommand = (BusinessUnitReply.SuccessCommand)reply;
            return new GetBusinessUnitResponse(successCommand.code(), successCommand.message());
        } else if(reply instanceof BusinessUnitReply.Domain){
            BusinessUnit businessUnit = ((BusinessUnitReply.Domain)reply).businessUnit();
            return new GetBusinessUnitResponse(businessUnit.id(), businessUnit.name(), businessUnit.active(), businessUnit.deleted());
        }
        return new GetBusinessUnitResponse(INVALID, "Something is wrong");
    }
}
