package com.jeromeloisel.db.conversion.jackson;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeromeloisel.db.conversion.api.JsonSerializer;
import com.jeromeloisel.db.entity.Entity;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;


@AllArgsConstructor(access=PACKAGE)
@FieldDefaults(level=PRIVATE, makeFinal=true)
final class JacksonJsonSerializer<T extends Entity> implements JsonSerializer<T> {
  @NonNull
  ObjectMapper mapper;
  
  @Override
  public String apply(final T t) {
    try {
      return mapper.writeValueAsString(t);
    } catch (final JsonProcessingException e) {
      throw new JacksonConversionException("Could not serialize entity=" + t.getClass(), e);
    }
  }

}
