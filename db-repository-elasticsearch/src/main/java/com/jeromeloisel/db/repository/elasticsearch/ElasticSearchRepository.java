package com.jeromeloisel.db.repository.elasticsearch;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.jeromeloisel.db.conversion.api.JsonDeserializer;
import com.jeromeloisel.db.conversion.api.JsonSerializer;
import com.jeromeloisel.db.entity.Entity;
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
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;

import java.util.List;
import java.util.Optional;

import static com.google.common.base.Strings.emptyToNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;
import static org.elasticsearch.action.support.WriteRequest.RefreshPolicy.IMMEDIATE;
import static org.elasticsearch.common.unit.TimeValue.timeValueMinutes;
import static org.elasticsearch.search.sort.SortBuilders.fieldSort;

@AllArgsConstructor(access = PACKAGE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@SuppressWarnings("unchecked")
final class ElasticSearchRepository<T extends Entity> implements ElasticRepository<T> {
  private static final TimeValue SCROLL_TIME = timeValueMinutes(1);
  protected static final int SCROLL_SIZE = 100;
  
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

  @Override
  public List<T> search(final QueryBuilder query) {
    SearchResponse scroll = client
        .prepareSearch(index)
        .setTypes(type)
        .setFetchSource(true)
        .setScroll(SCROLL_TIME)
        .setSize(SCROLL_SIZE)
        .setQuery(query)
        .addSort(fieldSort("_doc"))
        .execute()
        .actionGet();
    
    final Builder<T> builder = ImmutableList.builder();

    String scrollId = scroll.getScrollId();
    while(true) {
      final SearchHit[] hits = scroll.getHits().getHits();
      if(hits.length == 0) {
        break;
      }
      
      for(final SearchHit hit : hits) {
        final String source = hit.getSourceAsString();
        final T entity = (T) deserializer.apply(source).withId(hit.getId());
        builder.add(entity);
      }
      
      scroll = client
          .prepareSearchScroll(scrollId)
          .setScroll(SCROLL_TIME)
          .execute()
          .actionGet();
      scrollId = scroll.getScrollId();
    }

    return builder.build();
  }
  
  @Override
  public T save(final T entity) {
    return saveAll(ImmutableList.of(entity)).iterator().next();
  }

  @Override
  public List<T> saveAll(final List<T> entities) {
    if(entities.isEmpty()) {
      return entities;
    }
    
    final BulkRequestBuilder bulk = client
        .prepareBulk()
        .setRefreshPolicy(IMMEDIATE);
    
    for(final T entity : entities) {
      final IndexRequestBuilder request = client
          .prepareIndex(index, type)
          .setSource(serializer.apply(entity));
      ofNullable(emptyToNull(entity.getId())).ifPresent(request::setId);
      bulk.add(request);
    }
    
    final BulkResponse response = bulk.execute().actionGet();
    final BulkItemResponse[] items = response.getItems();
    
    final ImmutableList.Builder<T> saved = ImmutableList.builder();
    for(int i=0; i<items.length; i++) {
      final BulkItemResponse item = items[i];
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
    return findOne(id).isPresent();
  }

  @Override
  public Optional<T> findOne(final String id) {
    final GetResponse response = client
        .prepareGet(index, type, id)
        .setFetchSource(true)
        .execute()
        .actionGet();
    final String json = response.getSourceAsString();
    return Optional
        .ofNullable(json)
        .map(deserializer)
        .map(e -> (T) e.withId(id));
  }

  @Override
  public List<T> findAll(final List<String> ids) {
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
    return deleteAll(ImmutableList.of(entity)).iterator().next();
  }
  
  @Override
  public List<String> deleteAll(final List<T> entities) {
    return deleteAllIds(entities.stream().map(Entity::getId).collect(toList()));
  }
  
  @Override
  public String delete(final String id) {
    return deleteAllIds(ImmutableList.of(id)).iterator().next();
  }
  
  @Override
  public List<String> deleteAllIds(final List<String> ids) {
    final BulkRequestBuilder bulk = client
        .prepareBulk()
        .setRefreshPolicy(IMMEDIATE);
    
    for(final String id : ids) {
      bulk.add(client.prepareDelete(index, type, id));
    }
    
    final BulkResponse response = bulk.execute().actionGet();
    
    final ImmutableList.Builder<String> builder = ImmutableList.builder();
    for(final BulkItemResponse item : response.getItems()) {
      builder.add(item.getId());
    }
    return builder.build();
  }
}
