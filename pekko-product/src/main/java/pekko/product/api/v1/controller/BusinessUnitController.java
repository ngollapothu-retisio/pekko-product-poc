package pekko.product.api.v1.controller;

import lombok.extern.slf4j.Slf4j;
import pekko.product.api.v1.request.CreateBusinessUnitRequest;
import pekko.product.api.v1.request.PatchBusinessUnitRequest;
import pekko.product.api.v1.request.UpdateBusinessUnitRequest;
import pekko.product.api.v1.response.GetBusinessUnitResponse;
import pekko.product.api.v1.service.BusinessUnitService;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@Slf4j
public class BusinessUnitController extends Controller {

    @Inject
    private BusinessUnitService businessUnitService;

    private Result constructResult(GetBusinessUnitResponse r) {
        return Optional.ofNullable(r.getStatusCode()).map(t -> status(r.getStatusCode(), Json.toJson(r))).orElseGet(()->ok(Json.toJson(r)));
    }
    public CompletionStage<Result> createBusinessUnit(Http.Request request) {
        log.info("createBusinessUnit api invoked ....");
        CreateBusinessUnitRequest createBusinessUnitRequest = Json.fromJson(request.body().asJson(), CreateBusinessUnitRequest.class);
        return businessUnitService.createBusinessUnit(createBusinessUnitRequest)
                .thenApply(this::constructResult);
    }
    public CompletionStage<Result> updateBusinessUnit(Http.Request request) {
        log.info("updateBusinessUnit api invoked ....");
        UpdateBusinessUnitRequest createBusinessUnitRequest = Json.fromJson(request.body().asJson(), UpdateBusinessUnitRequest.class);
        return businessUnitService.updateBusinessUnit(createBusinessUnitRequest)
                .thenApply(this::constructResult);
    }
    public CompletionStage<Result> patchBusinessUnit(Http.Request request, String id) {
        log.info("patchBusinessUnit api invoked ....");
        PatchBusinessUnitRequest patchBusinessUnitRequest = Json.fromJson(request.body().asJson(), PatchBusinessUnitRequest.class);
        return businessUnitService.patchBusinessUnit(patchBusinessUnitRequest, id)
                .thenApply(this::constructResult);
    }
    public CompletionStage<Result> deleteBusinessUnit(Http.Request request, String id) {
        log.info("deleteBusinessUnit api invoked ....");
        return businessUnitService.deleteBusinessUnit(id)
                .thenApply(this::constructResult);
    }
    public CompletionStage<Result> getBusinessUnit(Http.Request request, String id) {
        log.info("getBusinessUnit api invoked ....");
        return businessUnitService.getBusinessUnit(id)
                .thenApply(this::constructResult);
    }
}