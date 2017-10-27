package com.jeromeloisel.database.scroll.elastic;


import com.google.common.collect.ImmutableSet;
import com.google.common.testing.NullPointerTester;
import com.jeromeloisel.db.scroll.api.DatabaseScroll;
import org.elasticsearch.search.SearchHit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static com.google.common.testing.NullPointerTester.Visibility.PACKAGE;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CompoundScrollTest {

  @Mock
  DatabaseScroll first;
  @Mock
  DatabaseScroll second;
  @Mock
  SearchHit hit;

  CompoundScroll scroll;

  @Before
  public void before() {
    scroll = new CompoundScroll(ImmutableSet.of(first, second));
  }

  @Test
  public void shouldOnStart() throws IOException {
    scroll.onStartBatch();
    verify(first).onStartBatch();
    verify(second).onStartBatch();
  }

  @Test
  public void shouldOnEnd() throws IOException {
    scroll.onEndBatch();
    verify(first).onEndBatch();
    verify(second).onEndBatch();
  }

  @Test
  public void shouldAccept() throws IOException {
    final CompoundScroll scroll = new CompoundScroll(ImmutableSet.of(first, second));
    scroll.accept(hit);
    verify(first).accept(hit);
    verify(second).accept(hit);
  }

  @Test
  public void shouldPassNPETester() {
    new NullPointerTester().testConstructors(CompoundScroll.class, PACKAGE);
  }
}
