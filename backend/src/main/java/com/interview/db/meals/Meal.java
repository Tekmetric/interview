package com.interview.db.meals;

import com.interview.db.User;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "meal",
uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "fkUser"})})
public class Meal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fkUser", referencedColumnName = "id")
  private User user;

  @Column(name = "name")
  private String name;

  @OneToMany(mappedBy = "meal")
  private Set<Ingredients> ingredients;

  protected Meal() {
  }

  public Meal(Integer id, User user, String name, Set<Ingredients> ingredients) {
    this.id = id;
    this.user = user;
    this.name = name;
    this.ingredients = ingredients;
  }

  public Integer getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public String getName() {
    return name;
  }

  public Set<Ingredients> getIngredients() {
    return ingredients;
  }

  /**
   * Note: this is terrible, but needed because of shortcomings of JPA.
   *
   * I have tried to avoid mixmatching mutability/immutability, but
   * it is not possible here.
   * @param user
   */
  public void setUser(User user) {
    this.user = user;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setIngredients(Set<Ingredients> ingredients) {
    this.ingredients = ingredients;
  }
}
