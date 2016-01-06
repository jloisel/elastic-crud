package com.jeromeloisel.db.repository.elasticsearch;

import static com.google.common.testing.NullPointerTester.Visibility.PACKAGE;

import org.junit.Test;

import com.google.common.testing.NullPointerTester;

public class ElasticSearchRepositoryTest {

  @Test
  public void shouldPassNPETester() {
    new NullPointerTester().testConstructors(ElasticSearchRepository.class, PACKAGE);
  }
}
