package com.jeromeloisel.db.integration.test;

import static org.junit.Assert.assertNotNull;

import org.elasticsearch.client.Client;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ESTest extends SpringElasticSearchTest {

  @Autowired
  private Client client;
  
  @Test
  public void shouldAutowire() {
    assertNotNull(client);
  }
}
