package com.jeromeloisel.database.scroll.elastic;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jeromeloisel.db.entity.Entity;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Wither;

import static com.google.common.base.Preconditions.checkNotNull;

@Value
@Builder
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
    this.id = checkNotNull(id);
    this.firstname = checkNotNull(firstname);
    this.lastname = checkNotNull(lastname);
  }


}
