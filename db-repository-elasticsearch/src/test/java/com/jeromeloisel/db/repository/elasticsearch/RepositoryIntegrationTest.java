package com.jeromeloisel.db.repository.elasticsearch;

import static com.google.common.base.Preconditions.checkState;
import static org.apache.commons.io.IOUtils.toByteArray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.jeromeloisel.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=Application.class)
public class RepositoryIntegrationTest {

  private static final String INDEX = "datas";

  private static final Person PERSON = Person
      .builder()
      .id("")
      .firstname("John")
      .lastname("Smith")
      .build();

  @Autowired
  private Client client;
  @Autowired
  private ElasticSearchRepositoryFactory factory;

  private ElasticRepository<Person> repository;

  @Before
  public void before() throws IOException {
    repository = factory.create(Person.class);

    final IndicesAdminClient indices = client.admin().indices();

    final PutIndexTemplateRequest datas = indices.preparePutTemplate(INDEX)
        .setSource(toByteArray(getClass().getResourceAsStream("/datas.json")))
        .request();
    checkState(indices.putTemplate(datas).actionGet().isAcknowledged());
  }

  @Test
  public void shouldSaveTwice() {
    final Builder<Person> builder = ImmutableList.builder();
    for(int i=0; i<ElasticSearchRepository.SCROLL_SIZE * 2;i++) {
      builder.add(PERSON);
    }
    final List<Person> persons = builder.build();

    final List<Person> saved = repository.saveAll(persons);
    assertEquals(persons.size(), saved.size());

    final List<Person> found = repository.search(new MatchAllQueryBuilder());
    assertEquals(persons.size(), found.size());

    repository.deleteAll(saved);
  }

  @Test
  public void shouldScanAndScroll() {
    final Person saved = repository.save(PERSON);
    final Person anotherSave = repository.save(saved);
    assertEquals(anotherSave.getId(), saved.getId());
    repository.delete(saved);
  }

  @Test
  public void shouldDeleteById() {
    final Person saved = repository.save(PERSON);
    repository.delete(saved.getId());
    assertFalse(repository.exists(saved));
  }

  @Test
  public void shouldExistsById() {
    final Person saved = repository.save(PERSON);
    assertTrue(repository.exists(saved.getId()));
    repository.delete(saved.getId());
    assertFalse(repository.exists(saved.getId()));
  }

  @Test
  public void shouldFindOne() {
    final Person save = repository.save(PERSON);
    assertEquals(save, repository.findOne(save.getId()).get());
    repository.delete(save);
  }

  @Test
  public void shouldFindAll() {
    final Person save = repository.save(PERSON);
    assertEquals(ImmutableList.of(save), repository.findAll(ImmutableList.of(save.getId())));
    repository.delete(save);
  }

  @Test
  public void shouldSaveAll() {
    final List<Person> saved = repository.saveAll(ImmutableList.of(PERSON));
    assertEquals(1, saved.size());
    repository.delete(saved.get(0));
  }

  @Test
  public void shouldFindByFirstname() {
    final Person saved = repository.save(PERSON);
    final TermQueryBuilder term = new TermQueryBuilder("firstname", PERSON.getFirstname());

    final List<Person> search = repository.search(term);
    assertEquals(1, search.size());
    assertEquals(saved, search.get(0));
    repository.delete(saved);
  }

  @Test
  public void shouldFindByFirstnameAndLastname() {
    final Person saved = repository.save(PERSON);
    final TermQueryBuilder byFirstname = new TermQueryBuilder("firstname", PERSON.getFirstname());
    final TermQueryBuilder byLastname = new TermQueryBuilder("lastname", PERSON.getLastname());
    final BoolQueryBuilder bool = new BoolQueryBuilder()
        .must(byFirstname)
        .must(byLastname);

    final List<Person> search = repository.search(bool);
    assertEquals(1, search.size());
    assertEquals(saved, search.get(0));
    repository.delete(saved);
  }

  @Test
  public void shouldTest() {
    final Person saved = repository.save(PERSON);
    assertNotEquals("", saved.getId());
    final String id = repository.delete(saved);
    assertEquals(saved.getId(), id);
  }

  @After
  public void after() {
    final List<Person> found = repository.search(new MatchAllQueryBuilder());
    if(found.isEmpty()) {
      return;
    }
    repository.deleteAll(found);
  }
}
