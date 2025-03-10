package pekko.product.application.event;

import pekko.product.application.serializer.JsonSerializable;

public interface ProductEvent extends JsonSerializable {
    record Created(String id, String name, Boolean active) implements ProductEvent {
    }
    record Updated(String id, String name, Boolean active) implements ProductEvent {
    }
    record Patched(String id, String name, Boolean active) implements ProductEvent {
    }
    record Deleted(String id) implements ProductEvent {
    }
}