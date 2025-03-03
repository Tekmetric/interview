package com.interview.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "vets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "animals")
@EqualsAndHashCode(of = "id")
public class Vet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vet_generator")
    @SequenceGenerator(name = "vet_generator", sequenceName = "vet_seq", allocationSize = 1)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Specialization is required")
    @Column(nullable = false)
    private String specialization;

    @NotBlank(message = "Contact information is required")
    @Email(message = "Contact information must be a valid email address")
    @Column(name = "contact_information", nullable = false)
    private String contactInformation;

    @ManyToMany(mappedBy = "vets")
    @Builder.Default
    private Set<Animal> animals = new HashSet<>();

    public void addAnimal(Animal animal) {
        animals.add(animal);
        animal.getVets().add(this);
    }

    public void removeAnimal(Animal animal) {
        animals.remove(animal);
        animal.getVets().remove(this);
    }

    public void removeAllAnimals() {
        animals.forEach(animal -> animal.getVets().remove(this));
        animals.clear();
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