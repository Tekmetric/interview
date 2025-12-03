package com.interview.domain.entity;

import com.interview.commands.CreatePersonCommand;
import com.interview.commands.UpsertPersonCommand;
import com.interview.domain.dto.Address;
import com.interview.domain.dto.Email;
import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Database representation of a Person.
 */
@SuppressWarnings("NotNullFieldNotInitialized")
@Entity
@Table(
        name = "person",
        uniqueConstraints = @UniqueConstraint(columnNames = "email"),
        indexes = @Index(columnList = "email")
)
public final class PersonEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true, updatable = false)
    private String email;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column
    private String phoneNumber;

    @Column
    private String addressCountryCode;
    @Column
    private String addressPostalCode;
    @Column
    private String addressAdministrativeArea;
    @Column
    private String addressLocality;
    @ElementCollection
    private List<String> addressLines;

    PersonEntity(
            UUID id,
            Email email,
            String firstName,
            String lastName,
            String phoneNumber,
            Address address
    ) {
        this.id = id;
        this.email = email.value();
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.addressCountryCode = address.countryCode();
        this.addressPostalCode = address.postalCode();
        this.addressAdministrativeArea = address.administrativeArea();
        this.addressLocality = address.locality();
        this.addressLines = address.addressLines();
    }

    public PersonEntity() {

    }

    /**
     * @param command {@code CreatePersonCommand} to build the entity from
     * @return {@code PersonEntity} from {@code command}
     */
    public static PersonEntity from(CreatePersonCommand command) {
        return new PersonEntity(
                UUID.randomUUID(),
                command.email(),
                command.firstName(),
                command.lastName(),
                command.phoneNumber(),
                command.address()
        );
    }

    /**
     * @param command {@code UpsertPersonCommand} to build the entity from
     * @return {@code PersonEntity} from {@code command}
     */
    public static PersonEntity from(UpsertPersonCommand command) {
        return new PersonEntity(
                UUID.randomUUID(),
                command.email(),
                command.firstName(),
                command.lastName(),
                command.phoneNumber(),
                command.address()
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEmail(Email email) {
        this.email = email.value();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddressCountryCode() {
        return addressCountryCode;
    }

    public void setAddressCountryCode(String addressCountryCode) {
        this.addressCountryCode = addressCountryCode;
    }

    public String getAddressPostalCode() {
        return addressPostalCode;
    }

    public void setAddressPostalCode(String addressPostalCode) {
        this.addressPostalCode = addressPostalCode;
    }

    public String getAddressAdministrativeArea() {
        return addressAdministrativeArea;
    }

    public void setAddressAdministrativeArea(String addressAdministrativeArea) {
        this.addressAdministrativeArea = addressAdministrativeArea;
    }

    public String getAddressLocality() {
        return addressLocality;
    }

    public void setAddressLocality(String addressLocality) {
        this.addressLocality = addressLocality;
    }

    public List<String> getAddressLines() {
        return addressLines;
    }

    public void setAddressLines(List<String> addressLines) {
        this.addressLines = addressLines;
    }

    public void setAddress(Address address) {
        addressCountryCode = address.countryCode();
        addressPostalCode = address.postalCode();
        addressAdministrativeArea = address.administrativeArea();
        addressLocality = address.locality();
        addressLines = address.addressLines();
    }

    public Address getAddress() throws IllegalArgumentException {
        return new Address(
                addressCountryCode,
                addressPostalCode,
                addressAdministrativeArea,
                addressLocality,
                addressLines
        );
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PersonEntity person = (PersonEntity) o;
        return Objects.equals(id, person.id) && Objects.equals(email, person.email) && Objects.equals(firstName, person.firstName) && Objects.equals(lastName, person.lastName) && Objects.equals(phoneNumber, person.phoneNumber) && Objects.equals(addressCountryCode, person.addressCountryCode) && Objects.equals(addressPostalCode, person.addressPostalCode) && Objects.equals(addressAdministrativeArea, person.addressAdministrativeArea) && Objects.equals(addressLocality, person.addressLocality) && Objects.equals(addressLines, person.addressLines);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, firstName, lastName, phoneNumber, addressCountryCode, addressPostalCode, addressAdministrativeArea, addressLocality, addressLines);
    }

    @Override
    public String toString() {
        return "Person[" +
                "id=" + id +
                ", email=" + email +
                ", firstName=" + firstName +
                ", lastName=" + lastName +
                ", phoneNumber=" + phoneNumber +
                ", address=" + getAddress() +
                ']';
    }
}
