package com.jeromeloisel.database.scroll.elastic;

import com.jeromeloisel.db.scroll.api.DatabaseScroll;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;

import java.util.concurrent.atomic.AtomicReference;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PACKAGE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class BulkDelete implements DatabaseScroll {
  @NonNull
  Client client;

  AtomicReference<BulkRequestBuilder> request = new AtomicReference<>();

  @Override
  public void onStartBatch() {
    request.set(client.prepareBulk());
  }

  @Override
  public void accept(final SearchHit hit) {
    final DeleteRequest delete = client
      .prepareDelete()
      .setIndex(hit.getIndex())
      .setType(hit.getType())
      .setId(hit.getId())
      .request();

    request.get().add(delete);
  }

  @Override
  public void onEndBatch() {
    request.get().execute().actionGet();
  }
}