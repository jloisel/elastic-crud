package com.jeromeloisel.database.scroll.elastic;

import com.jeromeloisel.db.scroll.api.DatabaseScroll;
import com.jeromeloisel.db.scroll.api.DatabaseScrolling;
import com.jeromeloisel.db.scroll.api.DatabaseScrollingFactory;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.springframework.stereotype.Service;

import static com.google.common.collect.ImmutableList.copyOf;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;
import static org.elasticsearch.common.unit.TimeValue.timeValueMinutes;

@Service
@AllArgsConstructor(access = PACKAGE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class ElasticScrollingFactory implements DatabaseScrollingFactory {
  private static final TimeValue DEFAULT_SCROLL_TIME = timeValueMinutes(1);
  public static final int DEFAULT_SCROLL_SIZE = 100;

  @NonNull
  Client client;

  @Override
  public DatabaseScrolling newScroll(final String index) {
    final SearchRequestBuilder search = client
      .prepareSearch(requireNonNull(index))
      .setFetchSource(true)
      .setSize(DEFAULT_SCROLL_SIZE);
    return newScroll(search);
  }

  @Override
  public DatabaseScrolling newScroll(final SearchRequestBuilder search) {
    return new ElasticScroll(client, search, DEFAULT_SCROLL_TIME);
  }

  @Override
  public DatabaseScroll bulkDelete() {
    return new BulkDelete(client);
  }

  @Override
  public DatabaseScroll bulkIndex() {
    return new BulkIndex(client);
  }

  @Override
  public DatabaseScroll combine(final DatabaseScroll... scrolls) {
    return new CompoundScroll(copyOf(scrolls));
  }
}
