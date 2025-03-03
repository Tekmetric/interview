package com.interview.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "animals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"responsibleEmployee", "vets"})
@EqualsAndHashCode(of = "id")
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "animal_generator")
    @SequenceGenerator(name = "animal_generator", sequenceName = "animal_seq", allocationSize = 1)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Species is required")
    @Column(nullable = false)
    private String species;

    @NotBlank(message = "Breed is required")
    @Column(nullable = false)
    private String breed;

    @PastOrPresent(message = "Date of birth must be in the past or today")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee responsibleEmployee;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "animal_vet",
        joinColumns = @JoinColumn(name = "animal_id"),
        inverseJoinColumns = @JoinColumn(name = "vet_id")
    )
    @Builder.Default
    private Set<Vet> vets = new HashSet<>();

    public void addVet(Vet vet) {
        vets.add(vet);
        vet.getAnimals().add(this);
    }

    public void updateVets(Set<Vet> newVets) {
        // Remove this animal from vets that are no longer associated
        vets.stream()
            .filter(vet -> !newVets.contains(vet))
            .forEach(vet -> vet.getAnimals().remove(this));

        // Add this animal to new vets
        newVets.stream()
            .filter(vet -> !vets.contains(vet))
            .forEach(vet -> vet.getAnimals().add(this));

        // Update the vets set
        vets.clear();
        vets.addAll(newVets);
    }

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        modifiedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedAt = LocalDateTime.now();
    }
}