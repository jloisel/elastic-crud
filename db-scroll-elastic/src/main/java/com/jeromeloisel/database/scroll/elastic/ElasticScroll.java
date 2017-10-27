package com.jeromeloisel.database.scroll.elastic;

import com.jeromeloisel.db.scroll.api.DatabaseScroll;
import com.jeromeloisel.db.scroll.api.DatabaseScrolling;
import lombok.experimental.FieldDefaults;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;
import static org.elasticsearch.search.sort.SortBuilders.fieldSort;

@FieldDefaults(level = PRIVATE, makeFinal = true)
final class ElasticScroll implements DatabaseScrolling {
  private static final FieldSortBuilder DEFAULT_SORT = fieldSort("_doc");

  Client client;
  SearchRequestBuilder search;
  AtomicReference<FieldSortBuilder> sort;
  AtomicReference<TimeValue> scrollTime;

  ElasticScroll(
    final Client client,
    final SearchRequestBuilder search,
    final TimeValue timeValue) {
    super();
    this.client = requireNonNull(client);
    this.search = requireNonNull(search);
    this.sort = new AtomicReference<>(DEFAULT_SORT);
    this.scrollTime = new AtomicReference<>(requireNonNull(timeValue));
  }

  @Override
  public DatabaseScrolling withSort(final FieldSortBuilder s) {
    this.sort.set(requireNonNull(s));
    return this;
  }

  @Override
  public DatabaseScrolling withScrollSize(final int size) {
    search.setSize(size);
    return this;
  }

  @Override
  public DatabaseScrolling withKeepAlive(final long time, final TimeUnit unit) {
    scrollTime.set(TimeValue.timeValueMillis(unit.toMillis(time)));
    return this;
  }

  @Override
  public DatabaseScrolling withTypes(final String... types) {
    search.setTypes(types);
    return this;
  }

  @Override
  public DatabaseScrolling withFetchSource(final boolean fetchSource) {
    search.setFetchSource(fetchSource);
    return this;
  }

  @Override
  public DatabaseScrolling withQuery(final QueryBuilder query) {
    search.setQuery(query);
    return this;
  }

  @Override
  public void scroll(final DatabaseScroll scroll) {
    SearchResponse response = search
      .addSort(sort.get())
      .setScroll(scrollTime.get())
      .execute()
      .actionGet();

    while (true) {
      final String scrollId = response.getScrollId();
      final SearchHit[] hits = response.getHits().getHits();
      if(hits.length == 0) {
        break;
      }

      try {
        scroll.onStartBatch();
        try {
          for (final SearchHit hit : hits) {
            scroll.accept(hit);
          }
        } finally {
          scroll.onEndBatch();
        }
      } catch (final IOException e) {
        // Ignore
      }

      response = client
        .prepareSearchScroll(scrollId)
        .setScroll(scrollTime.get())
        .execute()
        .actionGet();
    }
  }
}
