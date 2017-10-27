package com.jeromeloisel.db.scroll.api;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;

import java.util.concurrent.TimeUnit;

public interface DatabaseScrolling {

  /**
   * Defaults to _doc sort.
   *
   * @param sort
   * @return
   */
  DatabaseScrolling withSort(FieldSortBuilder sort);

  /**
   * Defaults to 100.
   *
   * @param size
   * @return
   */
  DatabaseScrolling withScrollSize(int size);

  /**
   * Defaults to 1 minute.
   *
   * @param time
   * @param unit
   * @return
   */
  DatabaseScrolling withKeepAlive(long time, TimeUnit unit);

  /**
   * Defaults to all index types.
   * @param types
   * @return
   */
  DatabaseScrolling withTypes(String... types);

  /**
   * Defaults to true.
   *
   * @param fetchSource if source should be fetched
   * @return
   */
  DatabaseScrolling withFetchSource(boolean fetchSource);

  /**
   * By default, uses {@link org.elasticsearch.index.query.MatchAllQueryBuilder}.
   *
   * @param query the query
   */
  DatabaseScrolling withQuery(QueryBuilder query);

  void scroll(DatabaseScroll scroll);
}
