package pekko.product.api.v1.service;


import pekko.product.api.v1.request.CreateBusinessUnitRequest;
import pekko.product.api.v1.request.PatchBusinessUnitRequest;
import pekko.product.api.v1.request.UpdateBusinessUnitRequest;
import pekko.product.api.v1.response.GetBusinessUnitResponse;

import java.util.concurrent.CompletionStage;

public interface BusinessUnitService {
    public CompletionStage<GetBusinessUnitResponse> getBusinessUnit(String id);
    public CompletionStage<GetBusinessUnitResponse> createBusinessUnit(CreateBusinessUnitRequest request);
    public CompletionStage<GetBusinessUnitResponse> updateBusinessUnit(UpdateBusinessUnitRequest request);
    public CompletionStage<GetBusinessUnitResponse> patchBusinessUnit(PatchBusinessUnitRequest request, String id);
    public CompletionStage<GetBusinessUnitResponse> deleteBusinessUnit(String id);
}