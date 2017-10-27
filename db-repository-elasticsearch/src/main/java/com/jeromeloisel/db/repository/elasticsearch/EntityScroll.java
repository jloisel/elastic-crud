package com.jeromeloisel.db.repository.elasticsearch;

import com.jeromeloisel.db.conversion.api.JsonDeserializer;
import com.jeromeloisel.db.entity.Entity;
import com.jeromeloisel.db.scroll.api.DatabaseScroll;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.elasticsearch.search.SearchHit;

import java.util.function.Consumer;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PACKAGE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class EntityScroll<T extends Entity> implements DatabaseScroll {
  @NonNull
  JsonDeserializer<T> deserializer;
  @NonNull
  Consumer<T> consumer;

  @Override
  public void accept(final SearchHit hit) {
    final String source = hit.getSourceAsString();
    @SuppressWarnings("unchecked")
    final T entity = (T) deserializer.apply(source).withId(hit.getId());
    consumer.accept(entity);
  }
}
