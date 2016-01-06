package com.jeromeloisel.db.conversion.api;

import java.util.function.Function;

import com.jeromeloisel.db.entity.Entity;

public interface JsonSerializer<T extends Entity> extends Function<T, String> {
  
}
