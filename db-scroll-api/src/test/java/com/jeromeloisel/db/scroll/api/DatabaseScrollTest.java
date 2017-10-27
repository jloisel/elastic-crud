package com.jeromeloisel.db.scroll.api;

import com.jeromeloisel.db.scroll.api.DatabaseScroll;
import org.junit.Test;

import java.io.IOException;

public class DatabaseScrollTest {

  private DatabaseScroll scroll = hit -> {

  };

  @Test
  public void shouldOnStart() throws IOException {
    scroll.onStartBatch();
  }

  @Test
  public void shouldOnend() throws IOException {
    scroll.onEndBatch();
  }
}
