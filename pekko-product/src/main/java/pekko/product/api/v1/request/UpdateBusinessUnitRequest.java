package pekko.product.api.v1.request;

public record UpdateBusinessUnitRequest(String id, String name, Boolean active) {}
