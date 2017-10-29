package com.jeromeloisel.db.integration.test;

import org.elasticsearch.client.IndicesAdminClient;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ElasticTest extends SpringElasticSearchTest {

  @Test
  public void shouldAutowire() {
    assertNotNull(client);
    final IndicesAdminClient indices = client.admin().indices();
    indices.prepareCreate("test").execute().actionGet();
    flush("test");
    indices.prepareDelete("test").execute().actionGet();
  }

  @Test
  @Override
  public void shouldAutowireClient() {
    super.shouldAutowireClient();
  }
}
