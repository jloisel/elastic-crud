package com.jeromeloisel.repository.elasticsearch.starter;

import com.jeromeloisel.Application;
import com.jeromeloisel.db.repository.api.DatabaseRepositoryFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class)
public class WiringTest {

  @Autowired
  DatabaseRepositoryFactory factory;
  
  @Test
  public void shouldAutowire() {
    assertNotNull(factory);
  }
}
