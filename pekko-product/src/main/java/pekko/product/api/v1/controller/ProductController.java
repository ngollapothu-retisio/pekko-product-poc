package pekko.product.api.v1.controller;

import lombok.extern.slf4j.Slf4j;
import pekko.product.api.v1.request.CreateProductRequest;
import pekko.product.api.v1.request.PatchProductRequest;
import pekko.product.api.v1.request.UpdateProductRequest;
import pekko.product.api.v1.response.GetProductResponse;
import pekko.product.api.v1.service.ProductService;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@Slf4j
public class ProductController extends Controller {

    @Inject
    private ProductService productService;

    private Result constructResult(GetProductResponse r) {
        return Optional.ofNullable(r.getStatusCode()).map(t -> status(r.getStatusCode(), Json.toJson(r))).orElseGet(()->ok(Json.toJson(r)));
    }
    public CompletionStage<Result> createProduct(Http.Request request) {
        log.info("createProduct api invoked ....");
        CreateProductRequest createProductRequest = Json.fromJson(request.body().asJson(), CreateProductRequest.class);
        return productService.createProduct(createProductRequest)
                .thenApply(this::constructResult);
    }
    public CompletionStage<Result> updateProduct(Http.Request request) {
        log.info("updateProduct api invoked ....");
        UpdateProductRequest createProductRequest = Json.fromJson(request.body().asJson(), UpdateProductRequest.class);
        return productService.updateProduct(createProductRequest)
                .thenApply(this::constructResult);
    }
    public CompletionStage<Result> patchProduct(Http.Request request, String id) {
        log.info("patchProduct api invoked ....");
        PatchProductRequest patchProductRequest = Json.fromJson(request.body().asJson(), PatchProductRequest.class);
        return productService.patchProduct(patchProductRequest, id)
                .thenApply(this::constructResult);
    }
    public CompletionStage<Result> deleteProduct(Http.Request request, String id) {
        log.info("deleteProduct api invoked ....");
        return productService.deleteProduct(id)
                .thenApply(this::constructResult);
    }
    public CompletionStage<Result> getProduct(Http.Request request, String id) {
        log.info("getProduct api invoked ....");
        return productService.getProduct(id)
                .thenApply(this::constructResult);
    }
}