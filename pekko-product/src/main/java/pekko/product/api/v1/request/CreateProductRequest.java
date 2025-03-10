package pekko.product.api.v1.request;

public record CreateProductRequest(String id, String name, Boolean active) {}
