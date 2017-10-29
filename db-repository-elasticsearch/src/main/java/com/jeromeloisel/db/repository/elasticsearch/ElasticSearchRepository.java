package com.jeromeloisel.db.repository.elasticsearch;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import com.jeromeloisel.db.conversion.api.JsonDeserializer;
import com.jeromeloisel.db.conversion.api.JsonSerializer;
import com.jeromeloisel.db.entity.Entity;
import com.jeromeloisel.db.repository.api.DatabaseRepository;
import com.jeromeloisel.db.scroll.api.DatabaseScrollingFactory;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.google.common.base.Strings.emptyToNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;
import static org.elasticsearch.common.xcontent.XContentType.JSON;

@AllArgsConstructor(access = PACKAGE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class ElasticSearchRepository<T extends Entity> implements DatabaseRepository<T> {
  @VisibleForTesting
  public static final int SCROLL_SIZE = 100;

  @NonNull
  String index;
  @NonNull
  String type;
  @NonNull
  Client client;
  @NonNull
  JsonSerializer<T> serializer;
  @NonNull
  JsonDeserializer<T> deserializer;
  @NonNull
  DatabaseScrollingFactory scrolling;
  @NonNull
  AtomicReference<WriteRequest.RefreshPolicy> policy;

  @Override
  public List<T> search(final QueryBuilder query) {
    final Builder<T> builder = ImmutableList.builder();
    scroll(query, builder::add);
    return builder.build();
  }

  @Override
  public void deleteAllByQuery(final QueryBuilder query) {
    final Builder<String> builder = ImmutableList.builder();
    scroll(query, e -> builder.add(e.getId()));
    final List<String> ids = builder.build();
    if(ids.isEmpty()) {
      return;
    }

    deleteAllIds(ids);
  }

  @Override
  public void scroll(final QueryBuilder query, final Consumer<T> consumer) {
    scrolling
      .newScroll(index)
      .withQuery(query)
      .withFetchSource(true)
      .withTypes(type)
      .scroll(new EntityScroll<>(deserializer, consumer));
  }

  @Override
  public T save(final T entity) {
    return saveAll(ImmutableList.of(entity)).get(0);
  }

  @Override
  public List<T> saveAll(final List<T> entities) {
    if(entities.isEmpty()) {
      return entities;
    }

    final BulkRequestBuilder bulk = client
      .prepareBulk()
      .setRefreshPolicy(policy.get());

    for(final T entity : entities) {
      final String source = serializer.apply(entity);
      final IndexRequestBuilder request = client
        .prepareIndex(index, type)
        .setSource(source, JSON);
      ofNullable(emptyToNull(entity.getId())).ifPresent(request::setId);
      bulk.add(request);
    }

    final BulkResponse response = bulk.execute().actionGet();
    final BulkItemResponse[] items = response.getItems();

    final ImmutableList.Builder<T> saved = ImmutableList.builder();
    for(int i=0; i<items.length; i++) {
      final BulkItemResponse item = items[i];
      @SuppressWarnings("unchecked")
      final T entity = (T) entities.get(i).withId(item.getId());
      saved.add(entity);
    }
    return saved.build();
  }

  @Override
  public boolean exists(final T entity) {
    return exists(entity.getId());
  }

  @Override
  public boolean exists(final String id) {
    final GetResponse response = client
      .prepareGet(index, type, id)
      .setFetchSource(false)
      .execute()
      .actionGet();
    return response.isExists();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Optional<T> findOne(final String id) {
    final GetResponse response = client
      .prepareGet(index, type, id)
      .setFetchSource(true)
      .execute()
      .actionGet();
    final String json = response.getSourceAsString();
    return ofNullable(json)
      .map(deserializer)
      .map(e -> (T) e.withId(id));
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<T> findAll(final List<String> ids) {
    if (ids.isEmpty()) {
      return ImmutableList.of();
    }

    final Builder<T> builder = ImmutableList.builder();

    final MultiGetResponse response = client
      .prepareMultiGet()
      .add(index, type, ids)
      .execute()
      .actionGet();

    for(final MultiGetItemResponse item : response.getResponses()) {
      final GetResponse get = item.getResponse();

      final String json = get.getSourceAsString();
      final T entity = deserializer.apply(json);
      builder.add((T) entity.withId(get.getId()));
    }

    return builder.build();
  }

  @Override
  public String delete(final T entity) {
    return deleteAll(ImmutableList.of(entity)).get(0);
  }

  @Override
  public List<String> deleteAll(final Collection<T> entities) {
    return deleteAllIds(entities.stream().map(Entity::getId).collect(toList()));
  }

  @Override
  public String delete(final String id) {
    return deleteAllIds(ImmutableSet.of(id)).get(0);
  }

  @Override
  public List<String> deleteAllIds(final Collection<String> ids) {
    if (ids.isEmpty()) {
      return ImmutableList.of();
    }

    final BulkRequestBuilder bulk = client
      .prepareBulk()
      .setRefreshPolicy(policy.get());

    for (final String id : ids) {
      bulk.add(client.prepareDelete(index, type, id));
    }

    final BulkResponse response = bulk.execute().actionGet();

    final ImmutableList.Builder<String> builder = ImmutableList.builder();
    for (final BulkItemResponse item : response.getItems()) {
      builder.add(item.getId());
    }
    return builder.build();
  }

  @Override
  public void refreshPolicy(final WriteRequest.RefreshPolicy refresh) {
    policy.set(refresh);
  }
}