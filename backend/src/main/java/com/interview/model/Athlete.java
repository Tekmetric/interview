package com.interview.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

@Entity
@Table(name = "athletes")
public class Athlete {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID athleteId;

    @NotBlank
    @Size(max = 100)
    @Column(name = "first_name")
    private String firstName;

    @NotBlank
    @Size(max = 100)
    @Column(name = "last_name")
    private String lastName;

    @NotNull
    @Column(name = "position")
    @Enumerated(EnumType.STRING)
    private Position position;

    @Column(name = "shoots")
    @Enumerated(EnumType.STRING)
    private Shoots shoots;

    @Column(name = "number")
    private Integer number;

    public Athlete() {
    }

    public Athlete(@NonNull String firstName, @NonNull String lastName, @NonNull Position position, Shoots shoots, Integer number) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.shoots = shoots;
        this.number = number;
    }

    @JsonProperty
    public UUID getAthleteId() {
        return athleteId;
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

    public @NonNull Position getPosition() {
        return position;
    }

    public void setPosition(@NonNull Position position) {
        this.position = position;
    }

    public Shoots getShoots() {
        return shoots;
    }

    public void setShoots(Shoots shoots) {
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
                ", position=" + position +
                ", shoots=" + shoots +
                ", number=" + number +
                '}';
    }
}
