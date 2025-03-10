package pekko.product.api.v1.request;

public record PatchBusinessUnitRequest(String name, Boolean active) {}
