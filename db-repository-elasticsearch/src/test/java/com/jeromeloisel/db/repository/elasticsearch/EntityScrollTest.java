package com.jeromeloisel.db.repository.elasticsearch;

import com.google.common.testing.NullPointerTester;
import org.junit.Test;

import static com.google.common.testing.NullPointerTester.Visibility.PACKAGE;

public class EntityScrollTest {

  @Test
  public void shouldPassNPETester() {
    new NullPointerTester().testConstructors(EntityScroll.class, PACKAGE);
  }
}
