package pekko.product.api.v1.request;

public record CreateBusinessUnitRequest(String id, String name, Boolean active) {}