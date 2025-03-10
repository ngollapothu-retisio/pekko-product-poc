package pekko.product.api.v1.request;

public record PatchProductRequest(String name, Boolean active) {}
