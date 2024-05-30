package com.interview.model.meals.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO for {@link com.interview.db.User}
 */
public class UserDto implements Serializable {

  private Integer id;
  private String username;
  private String first;
  private String last;

  protected UserDto() {}

  public UserDto(Integer id, String username, String first, String last) {
    this.id = id;
    this.username = username;
    this.first = first;
    this.last = last;
  }

  public Integer getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getFirst() {
    return first;
  }

  public String getLast() {
    return last;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserDto entity = (UserDto) o;
    return Objects.equals(this.id, entity.id) &&
        Objects.equals(this.username, entity.username) &&
        Objects.equals(this.first, entity.first) &&
        Objects.equals(this.last, entity.last);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username, first, last);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "(" +
        "id = " + id + ", " +
        "username = " + username + ", " +
        "first = " + first + ", " +
        "last = " + last + ")";
  }
}