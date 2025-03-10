package pekko.product.api.v1.request;

public record UpdateProductRequest(String id, String name, Boolean active) {}
