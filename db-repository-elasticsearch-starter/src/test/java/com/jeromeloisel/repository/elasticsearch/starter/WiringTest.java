package com.jeromeloisel.repository.elasticsearch.starter;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jeromeloisel.Application;
import com.jeromeloisel.db.repository.api.DatabaseRepositoryFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=Application.class)
public class WiringTest {

  @Autowired
  DatabaseRepositoryFactory factory;
  
  @Test
  public void shouldAutowire() {
    assertNotNull(factory);
  }
}
