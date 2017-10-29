package com.jeromeloisel.db.integration.test;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;

public class ESTest extends SpringElasticSearchTest {

  @Autowired
  private Client client;
  
  @Test
  public void shouldAutowire() {
    assertNotNull(client);
    final IndicesAdminClient indices = client.admin().indices();
    indices.prepareCreate("test").execute().actionGet();
    flush("test");
    indices.prepareDelete("test").execute().actionGet();
  }
}
