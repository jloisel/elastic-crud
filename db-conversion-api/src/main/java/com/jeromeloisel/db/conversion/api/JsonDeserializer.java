package com.jeromeloisel.db.conversion.api;

import java.util.function.Function;

import com.jeromeloisel.db.entity.Entity;

public interface JsonDeserializer<T extends Entity> extends Function<String, T> {
  
}
