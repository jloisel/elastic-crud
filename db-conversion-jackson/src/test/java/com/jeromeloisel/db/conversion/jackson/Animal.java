package com.jeromeloisel.db.conversion.jackson;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jeromeloisel.db.entity.Entity;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.Wither;

@Value
@Builder
public class Animal implements Entity {

  @Wither
  String id;
  String name;
  String specy;
  boolean isNude;
  
  @JsonCreator
  public Animal(
      @JsonProperty("id") final String id, 
      @JsonProperty("name") final String name, 
      @JsonProperty("specy") final String specy, 
      @JsonProperty("nude") final boolean isNude) {
    super();
    this.id = checkNotNull(id);
    this.name = checkNotNull(name);
    this.specy = checkNotNull(specy);
    this.isNude = checkNotNull(isNude);
  }

}
