package com.interview.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

@Entity
@Table(name="Athletes")
public class Athlete {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @JsonProperty
    private UUID athleteId;

    @Nonnull
    @Column(name = "first_name")
    private String firstName;

    @Nonnull
    @Column(name = "last_name")
    private String lastName;

    @Nonnull
    @Column(name = "position")
    private String position;

    @Column(name = "shoots")
    private String shoots;

    @Column(name = "number")
    private Integer number;

    public Athlete() {
    }

    public Athlete(@NonNull String firstName, @NonNull String lastName, @NonNull String position, String shoots, Integer number) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.shoots = shoots;
        this.number = number;
    }

    public @NonNull String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NonNull String firstName) {
        this.firstName = firstName;
    }

    public @NonNull String getLastName() {
        return lastName;
    }

    public void setLastName(@NonNull String lastName) {
        this.lastName = lastName;
    }

    public @NonNull String getPosition() {
        return position;
    }

    public void setPosition(@NonNull String position) {
        this.position = position;
    }

    public String getShoots() {
        return shoots;
    }

    public void setShoots(String shoots) {
        this.shoots = shoots;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "Athlete{" +
                "athleteId=" + athleteId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", position='" + position + '\'' +
                ", shoots='" + shoots + '\'' +
                ", number=" + number +
                '}';
    }
}
