package com.jeromeloisel.db.scroll.api;

import org.elasticsearch.search.SearchHit;

import java.io.IOException;

@FunctionalInterface
public interface DatabaseScroll {

  default void onStartBatch() throws IOException {

  }

  void accept(SearchHit hit) throws IOException;

  default void onEndBatch() throws IOException {

  }
}
