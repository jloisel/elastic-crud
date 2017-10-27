package com.jeromeloisel.database.scroll.elastic;

import com.google.common.testing.NullPointerTester;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.TimeUnit;

import static com.google.common.testing.NullPointerTester.Visibility.PACKAGE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ElasticScrollTest {

  @Mock
  Client client;
  @Mock
  SearchRequestBuilder search;

  private ElasticScroll scroll;

  @Before
  public void before() {
    scroll = new ElasticScroll(client, search, TimeValue.ZERO);
  }

  @Test
  public void shouldFetchSource() {
    scroll.withFetchSource(true);
    verify(search).setFetchSource(true);
  }

  @Test
  public void shouldWithKeepAlive() {
    scroll.withKeepAlive(1, TimeUnit.SECONDS);
  }

  @Test
  public void shouldWithQuery() {
    final QueryBuilder query = new MatchAllQueryBuilder();
    scroll.withQuery(query);
    verify(search).setQuery(query);
  }

  @Test
  public void shouldWithSort() {
    final FieldSortBuilder sort = new FieldSortBuilder("_doc");
    scroll.withSort(sort);
  }

  @Test
  public void shouldWithSize() {
    scroll.withScrollSize(123);
    verify(search).setSize(123);
  }

  @Test
  public void shoudlWithTypes() {
    scroll.withTypes("type");
    verify(search).setTypes("type");
  }

  @Test
  public void shouldPassNPETester() {
    new NullPointerTester()
      .setDefault(SearchRequestBuilder.class, mock(SearchRequestBuilder.class))
      .testConstructors(ElasticScroll.class, PACKAGE);
  }
}
