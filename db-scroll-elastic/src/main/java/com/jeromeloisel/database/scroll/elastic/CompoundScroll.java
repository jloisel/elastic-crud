package com.jeromeloisel.database.scroll.elastic;

import com.jeromeloisel.db.scroll.api.DatabaseScroll;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.elasticsearch.search.SearchHit;

import java.io.IOException;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PACKAGE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class CompoundScroll implements DatabaseScroll {
  @NonNull
  Iterable<DatabaseScroll> scrolls;

  @Override
  public void onStartBatch() throws IOException {
    for (final DatabaseScroll scroll : scrolls) {
      scroll.onStartBatch();
    }
  }

  @Override
  public void accept(final SearchHit hit) throws IOException {
    for (final DatabaseScroll consumer : scrolls) {
      consumer.accept(hit);
    }
  }

  @Override
  public void onEndBatch() throws IOException {
    for (final DatabaseScroll scroll : scrolls) {
      scroll.onEndBatch();
    }
  }
}
