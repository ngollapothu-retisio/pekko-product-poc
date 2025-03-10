package pekko.product.application.domain;


import pekko.product.application.serializer.JsonSerializable;

public record BusinessUnit(String id, String name, Boolean active, Boolean deleted) implements JsonSerializable {
}
