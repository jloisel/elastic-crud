package com.jeromeloisel.database.scroll.elastic;

import com.google.common.testing.NullPointerTester;
import com.jeromeloisel.db.scroll.api.DatabaseScroll;
import org.elasticsearch.client.Client;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.google.common.testing.NullPointerTester.Visibility.PACKAGE;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ElasticScrollingFactoryTest {

  @Mock
  Client client;
  @Mock
  DatabaseScroll scroll;

  @Test
  public void shouldCombine() {
    final ElasticScrollingFactory factory = new ElasticScrollingFactory(client);
    final DatabaseScroll combine = factory.combine(scroll);
    assertEquals(CompoundScroll.class, combine.getClass());
  }

  @Test
  public void shouldPassNPETester() {
    new NullPointerTester().testConstructors(ElasticScrollingFactory.class, PACKAGE);
  }
}
