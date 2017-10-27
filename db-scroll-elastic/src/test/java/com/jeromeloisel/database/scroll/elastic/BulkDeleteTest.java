package com.jeromeloisel.database.scroll.elastic;


import com.google.common.testing.NullPointerTester;
import org.junit.Test;

import static com.google.common.testing.NullPointerTester.Visibility.PACKAGE;

public class BulkDeleteTest {

  @Test
  public void shouldPassNPETester() {
    new NullPointerTester().testConstructors(BulkDelete.class, PACKAGE);
  }
}
