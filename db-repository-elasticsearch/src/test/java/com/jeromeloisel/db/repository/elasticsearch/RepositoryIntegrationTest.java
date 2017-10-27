package com.jeromeloisel.db.repository.elasticsearch;

import com.google.common.collect.ImmutableList;
import com.jeromeloisel.Application;
import com.jeromeloisel.db.repository.api.DatabaseRepository;
import com.jeromeloisel.db.repository.api.DatabaseRepositoryFactory;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static org.apache.commons.io.IOUtils.toByteArray;
import static org.elasticsearch.action.support.WriteRequest.RefreshPolicy.IMMEDIATE;
import static org.elasticsearch.common.xcontent.XContentType.JSON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class)
public class RepositoryIntegrationTest {

  private static final Person PERSON = Person
    .builder()
    .id("")
    .firstname("John")
    .lastname("Smith")
    .build();

  @Autowired
  private Client client;
  @Autowired
  private DatabaseRepositoryFactory factory;

  private DatabaseRepository<Person> repository;

  @Before
  public void before() throws IOException {
    repository = factory.create(Person.class);
    repository.refreshPolicy(IMMEDIATE);
    final IndicesAdminClient indices = client.admin().indices();

    final PutIndexTemplateRequest datas = indices.preparePutTemplate("datas")
      .setSource(toByteArray(getClass().getResourceAsStream("/datas.json")), JSON)
      .request();
    checkState(indices.putTemplate(datas).actionGet().isAcknowledged());
  }

  @Test
  public void shouldSaveTwice() {
    final ImmutableList.Builder<Person> builder = ImmutableList.builder();
    for(int i=0; i< ElasticSearchRepository.SCROLL_SIZE * 2;i++) {
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
  public void shouldDeleteAllByQuery() {
    final Person saved = repository.save(PERSON);
    final TermQueryBuilder byFirstname = new TermQueryBuilder("firstname", PERSON.getFirstname());
    repository.deleteAllByQuery(byFirstname);
    assertFalse(repository.exists(saved));
  }

  @Test
  public void shouldNotDeleteAllByIds() {
    final List<String> ids = repository.deleteAllIds(ImmutableList.of());
    assertTrue(ids.isEmpty());
  }

  @Test
  public void shouldNotDeleteAllByQuery() {
    final Person saved = repository.save(PERSON);
    final TermQueryBuilder byFirstname = new TermQueryBuilder("firstname", PERSON.getLastname());
    repository.deleteAllByQuery(byFirstname);
    assertTrue(repository.exists(saved));
    repository.delete(saved);
  }

  @Test
  public void shouldNotDeleteById() {
    final Person saved = repository.save(PERSON);
    repository.delete(saved.getId() + "invalid");
    assertTrue(repository.exists(saved));
    repository.delete(saved.getId());
    assertFalse(repository.exists(saved));
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
  public void shouldNotFindAll() {
    assertEquals(ImmutableList.of(), repository.findAll(ImmutableList.of()));
  }

  @Test
  public void shouldNotFindAllIfInvalid() {
    assertEquals(ImmutableList.of(), repository.findAll(ImmutableList.of("invalid")));
  }

  @Test
  public void shouldSaveAll() {
    final List<Person> saved = repository.saveAll(ImmutableList.of(PERSON));
    assertEquals(1, saved.size());
    repository.delete(saved.get(0));
  }

  @Test
  public void shouldNotSaveAll() {
    final List<Person> saved = repository.saveAll(ImmutableList.of());
    assertEquals(0, saved.size());
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
    repository.delete(PERSON);
  }
}
