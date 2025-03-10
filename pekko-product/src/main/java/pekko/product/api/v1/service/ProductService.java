package pekko.product.api.v1.service;


import pekko.product.api.v1.request.CreateProductRequest;
import pekko.product.api.v1.request.PatchProductRequest;
import pekko.product.api.v1.request.UpdateProductRequest;
import pekko.product.api.v1.response.GetProductResponse;

import java.util.concurrent.CompletionStage;

public interface ProductService {
    public CompletionStage<GetProductResponse> getProduct(String id);
    public CompletionStage<GetProductResponse> createProduct(CreateProductRequest request);
    public CompletionStage<GetProductResponse> updateProduct(UpdateProductRequest request);
    public CompletionStage<GetProductResponse> patchProduct(PatchProductRequest request, String id);
    public CompletionStage<GetProductResponse> deleteProduct(String id);
}