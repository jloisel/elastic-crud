package com.jeromeloisel.db.repository.elasticsearch;

import com.jeromeloisel.db.conversion.api.JsonDeserializer;
import com.jeromeloisel.db.conversion.api.JsonSerializationFactory;
import com.jeromeloisel.db.conversion.api.JsonSerializer;
import com.jeromeloisel.db.entity.Document;
import com.jeromeloisel.db.entity.Entity;
import com.jeromeloisel.db.repository.api.DatabaseRepository;
import com.jeromeloisel.db.repository.api.DatabaseRepositoryFactory;
import com.jeromeloisel.db.scroll.api.DatabaseScrollingFactory;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.elasticsearch.client.Client;

import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Preconditions.checkState;
import static lombok.AccessLevel.PRIVATE;
import static org.elasticsearch.action.support.WriteRequest.RefreshPolicy.IMMEDIATE;

@AllArgsConstructor
@FieldDefaults(level=PRIVATE, makeFinal = true)
public final class ElasticSearchRepositoryFactory implements DatabaseRepositoryFactory {
  @NonNull
  JsonSerializationFactory factory;
  @NonNull
  Client client;
  @NonNull
  DatabaseScrollingFactory scrolling;

  @Override
  public <T extends Entity> DatabaseRepository<T> create(final Class<T> clazz) {
    checkState(
      clazz.isAnnotationPresent(Document.class),
      "%s must be annotated with @Document",
      clazz.getName());

    final Document document = clazz.getDeclaredAnnotation(Document.class);
    final JsonSerializer<T> serializer = factory.serializer(clazz);
    final JsonDeserializer<T> deserializer = factory.deserializer(clazz);

    final String index = document.indexName();
    final String type = document.type();

    return new ElasticSearchRepository<>(
      index,
      type,
      client,
      serializer,
      deserializer,
      scrolling,
      new AtomicReference<>(IMMEDIATE)
    );
  }
}
