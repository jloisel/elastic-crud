package com.jeromeloisel.db.integration.test;

import com.jeromeloisel.Application;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class)
public abstract class SpringElasticSearchTest {
  @Autowired
  protected Client client;

  @Test
  public void shouldAutowireClient() {
    assertNotNull(client);
  }

  protected final Client elasticClient() {
    return client;
  }

  protected final void flush(final String index) {
    final IndicesAdminClient indices = elasticClient().admin().indices();
    indices.prepareFlush(index).execute().actionGet();
    indices.prepareRefresh(index).execute().actionGet();
  }
}
