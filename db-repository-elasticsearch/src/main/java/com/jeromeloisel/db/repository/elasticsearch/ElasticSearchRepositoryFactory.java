package com.jeromeloisel.db.repository.elasticsearch;

import static com.google.common.base.Preconditions.checkState;
import static lombok.AccessLevel.PRIVATE;

import org.elasticsearch.client.Client;

import com.jeromeloisel.db.conversion.api.JsonDeserializer;
import com.jeromeloisel.db.conversion.api.JsonSerializationFactory;
import com.jeromeloisel.db.conversion.api.JsonSerializer;
import com.jeromeloisel.db.entity.Document;
import com.jeromeloisel.db.entity.Entity;
import com.jeromeloisel.db.repository.api.DatabaseRepositoryFactory;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@FieldDefaults(level=PRIVATE, makeFinal = true)
public final class ElasticSearchRepositoryFactory implements DatabaseRepositoryFactory {
  @NonNull
  JsonSerializationFactory factory;
  @NonNull
  Client client;
  
  @Override
  public <T extends Entity> ElasticRepository<T> create(final Class<T> clazz) {
    checkState(
        clazz.isAnnotationPresent(Document.class), 
        "%s must be annotated with @Document", 
        clazz.getName());
    
    final Document document = clazz.getDeclaredAnnotation(Document.class);
    final JsonSerializer<T> serializer = factory.serializer(clazz);
    final JsonDeserializer<T> deserializer = factory.deserializer(clazz);
    
    return new ElasticSearchRepository<>(
        document.indexName(), 
        document.type(), 
        client, 
        serializer, 
        deserializer);
  }

}
