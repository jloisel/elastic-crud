[![Build Status](https://travis-ci.org/jloisel/elastic-crud.svg)](https://travis-ci.org/jloisel/elastic-crud)
[![Dependency Status](https://www.versioneye.com/user/projects/568d2e269c1b98002b000030/badge.svg?style=flat)](https://www.versioneye.com/user/projects/568d2e269c1b98002b000030)

## Elasticsearch Simple CRUD Repository

Easily perform Create / Read / Update / Delete operations on beans stored in Elasticsearch. [Spring Data Elasticsearch](https://github.com/spring-projects/spring-data-elasticsearch) lacks maintenance and is already a few Elasticsearch versions behind the latest version.

### Maven configuration

Add the following Maven dependency to get started quickly with Spring:

```xml
<dependency>
    <groupId>com.jeromeloisel</groupId>
    <artifactId>db-repository-elasticsearch-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```
(Not released yet)

### Java Example

Suppose we would like to persist the following Bean in Elasticsearch:

```java
@Value
@Builder
@Document(indexName="datas", type="person")
public class Person implements Entity {
  @Wither
  String id;
  String firstname;
  String lastname;
  
  @JsonCreator
  Person(
      @JsonProperty("id") final String id, 
      @JsonProperty("firstname") final String firstname, 
      @JsonProperty("lastname") final String lastname) {
    super();
    this.id = id;
    this.firstname = checkNotNull(firstname);
    this.lastname = checkNotNull(lastname);
  }
} 
```

The following code shows how to use the CRUD repository:

```java
@Autowired
private ElasticSearchRepositoryFactory factory;

public void method() {
  final ElasticRepository<Person> repository = factory.create(Person.class);
  
  final Person person = Person.builder().id("").firstname("John").lastname("Smith").build();
  final Person withId = repository.save(person);
  
  // Find by id
  final Optional<Person> byId = repository.findOne(withId.getId());
  
  assertTrue(repository.exists(byId));
  
  // Search by firstname (with "not_analyzed" string mapping)
  final TermQueryBuilder term = new TermQueryBuilder("firstname", PERSON.getFirstname());
  final List<Person> found = repository.search(term);
  assertTrue(found.contains(byId));
  
  // Delete from Elasticsearch definitively
  repository.delete(withId.getId());

  assertFalse(repository.exists(byId));
}
```

### Type mapping

Beans stored in Elasticsearch must have **_source** field enabled: see https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-source-field.html. The following example Json shows how to enable _source field:

```json
{
  "template": "datas",
  "settings": {
    "number_of_shards": 5,
    "number_of_replicas": 1,
    "index.refresh_interval": -1,
  },
  "mappings": {
    "_default_": {
      "_all": {
          "enabled": false
       },
       "_source": {
          "enabled": true
       },
       "dynamic_templates": [
         {
           "strings": {
             "match_mapping_type": "string",
             "mapping": {
               "type": "string",
               "index": "not_analyzed"
             }
           }
         }
       ]
    }
  }
}
```

### Index refresh

Every mutating query (insert, delete) performed on the index automatically refreshes it. I would recommend to disable index refresh as shows in the Json above.

### Json Serialization

The Json serialization is configured to use [Jackson](https://github.com/FasterXML/jackson) by default. To use Jackson Json serialization, simply add Jackson as dependency:

```xml
<dependency>
	<groupId>com.fasterxml.jackson.core</groupId>
	<artifactId>jackson-databind</artifactId>
	<version>${jackson.version}</version>
</dependency>
```

Replace **${jackson.version}** with the version you are using.

If you intend to use your own Json serialization mechanism (like Gson), please provide an implementation for the **JsonSerializationFactory** interface.

### Elasticsearch Client

An instance of the [Elasticsearch Client](https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/client.html) must be provided.
