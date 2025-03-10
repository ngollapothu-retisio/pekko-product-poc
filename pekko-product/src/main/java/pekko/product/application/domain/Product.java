package pekko.product.application.domain;

import pekko.product.application.serializer.JsonSerializable;

public record Product(String id, String name, Boolean active, Boolean deleted) implements JsonSerializable {}