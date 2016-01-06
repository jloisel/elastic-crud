package com.jeromeloisel.db.conversion.api;

import com.jeromeloisel.db.entity.Entity;

public interface JsonSerializationFactory {

  <T extends Entity> JsonSerializer<T> serializer(Class<T> clazz);
  
  <T extends Entity> JsonDeserializer<T> deserializer(Class<T> clazz);
}
