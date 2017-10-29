package com.jeromeloisel.database.scroll.elastic;

import com.jeromeloisel.db.conversion.api.JsonSerializer;
import com.jeromeloisel.db.conversion.jackson.JacksonSerializationFactory;
import com.jeromeloisel.db.integration.test.SpringElasticSearchTest;
import com.jeromeloisel.db.scroll.api.DatabaseScrollingFactory;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.IndicesAdminClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.elasticsearch.common.xcontent.XContentType.JSON;

public class ElasticScrollingFactoryESTest extends SpringElasticSearchTest {
  private static final String INDEX = "data";
  private static final String TYPE = "person";
  private static final int SIZE = ElasticScrollingFactory.DEFAULT_SCROLL_SIZE + 1;

  @Autowired
  private JacksonSerializationFactory mapper;
  @Autowired
  private DatabaseScrollingFactory scroll;

  @Before
  public void before() throws IOException {
    if(!client.admin().indices().prepareExists(INDEX).execute().actionGet().isExists()) {
      client.admin().indices().prepareCreate(INDEX).execute().actionGet();
    }
    final JsonSerializer<Person> serializer = mapper.serializer(Person.class);
    final BulkRequestBuilder bulk = client.prepareBulk();
    for (int i = 0; i < SIZE; i++) {

      final String name = UUID.randomUUID().toString();
      final IndexRequest request = new IndexRequest(INDEX, TYPE);
      request.source(serializer.apply(Person.builder().id("").firstname(name).lastname(name).build()), JSON);
      bulk.add(request);
    }

    client.bulk(bulk.request()).actionGet();
    flush(INDEX);
  }

  @Test
  public void shouldScroll() {
    final AtomicLong atomic = new AtomicLong();
    scroll.newScroll(INDEX).scroll(hit -> atomic.incrementAndGet());
    Assert.assertEquals(
      SIZE, atomic.longValue());
  }

  @After
  public void after() {
    final IndicesAdminClient indices = client.admin().indices();
    if(indices.prepareExists(INDEX).execute().actionGet().isExists()) {
      indices.prepareDelete(INDEX).execute().actionGet();
    }
  }
}
