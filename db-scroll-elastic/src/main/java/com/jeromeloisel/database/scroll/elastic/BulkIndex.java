package com.jeromeloisel.database.scroll.elastic;

import com.jeromeloisel.db.scroll.api.DatabaseScroll;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;

import java.util.concurrent.atomic.AtomicReference;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;
import static org.elasticsearch.common.xcontent.XContentType.JSON;

@AllArgsConstructor(access = PACKAGE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class BulkIndex implements DatabaseScroll {
  @NonNull
  Client client;

  AtomicReference<BulkRequestBuilder> request = new AtomicReference<>();

  @Override
  public void onStartBatch() {
    request.set(client.prepareBulk());
  }

  @Override
  public void accept(final SearchHit hit) {
    final IndexRequest index = client
      .prepareIndex(hit.getIndex(), hit.getType(), hit.getId())
      .setSource(hit.getSourceRef(), JSON)
      .request();

    request.get().add(index);
  }

  @Override
  public void onEndBatch() {
    request.get().execute().actionGet();
  }
}