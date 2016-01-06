package com.jeromeloisel.db.repository.elasticsearch;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jeromeloisel.db.entity.Document;
import com.jeromeloisel.db.entity.Entity;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.Wither;

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
