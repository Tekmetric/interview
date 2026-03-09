package com.interview.repository.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import com.interview.domain.PhoneNumber;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.UUID;

@Entity(name = "customer")
public class CustomerEntity implements Identifiable {
    @Id
    private UUID id = UuidCreator.getTimeOrderedEpoch();

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "phone_number", unique = true, nullable = false, length = 20)
    private PhoneNumber phoneNumber;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CustomerEntity that)) return false;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
