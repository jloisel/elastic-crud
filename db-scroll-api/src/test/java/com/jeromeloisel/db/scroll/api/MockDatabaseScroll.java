package com.jeromeloisel.db.scroll.api;

import com.jeromeloisel.db.scroll.api.DatabaseScroll;
import com.jeromeloisel.db.scroll.api.DatabaseScrolling;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;

import java.util.concurrent.TimeUnit;

public final class MockDatabaseScroll implements DatabaseScrolling {
  @Override
  public DatabaseScrolling withSort(FieldSortBuilder sort) {
    return this;
  }

  @Override
  public DatabaseScrolling withScrollSize(int size) {
    return this;
  }

  @Override
  public DatabaseScrolling withKeepAlive(long time, TimeUnit unit) {
    return this;
  }

  @Override
  public DatabaseScrolling withTypes(String... types) {
    return this;
  }

  @Override
  public DatabaseScrolling withFetchSource(boolean fetchSource) {
    return this;
  }

  @Override
  public DatabaseScrolling withQuery(QueryBuilder query) {
    return this;
  }

  @Override
  public void scroll(final DatabaseScroll scroll) {
    // does nothing
  }
}
