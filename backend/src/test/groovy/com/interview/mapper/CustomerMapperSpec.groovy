package com.interview.mapper

import java.time.LocalDate

import org.mapstruct.factory.Mappers
import spock.lang.Specification
import spock.lang.Subject

import com.interview.persistence.entity.Customer
import com.interview.persistence.entity.embedded.Address
import com.interview.persistence.entity.embedded.EmploymentDetails
import com.interview.persistence.enums.EmploymentStatus
import com.interview.dto.request.CreateCustomerRequest
import com.interview.dto.request.UpdateCustomerRequest
import com.interview.dto.request.embedded.AddressRequest
import com.interview.dto.request.embedded.EmploymentDetailsRequest

class CustomerMapperSpec extends Specification {

    @Subject
    CustomerMapper mapper = Mappers.getMapper(CustomerMapper)

    def "toResponse masks the SSN to ***-**-XXXX"() {
        given:
        def customer = customerWithSsn("123-45-6789")

        when:
        def response = mapper.toResponse(customer)

        then:
        response.ssn == "***-**-6789"
    }

    def "toResponse does not expose raw SSN digits beyond the last four"() {
        given:
        def customer = customerWithSsn("987-65-4321")

        when:
        def response = mapper.toResponse(customer)

        then:
        !response.ssn.contains("987")
        !response.ssn.contains("65")
        response.ssn == "***-**-4321"
    }

    def "toResponse flattens embedded Address into top-level response fields"() {
        given:
        def customer = customerWithSsn("123-45-6789")
        customer.address = new Address(street: "100 Main St", city: "Austin", state: "TX", zipCode: "78701")

        when:
        def response = mapper.toResponse(customer)

        then:
        response.street   == "100 Main St"
        response.city     == "Austin"
        response.state    == "TX"
        response.zipCode  == "78701"
    }

    def "toResponse flattens embedded EmploymentDetails into top-level response fields"() {
        given:
        def customer = customerWithSsn("123-45-6789")
        customer.employmentDetails = new EmploymentDetails(
                employmentStatus: EmploymentStatus.EMPLOYED,
                employerName: "Acme Corp",
                annualIncome: 95000.00
        )

        when:
        def response = mapper.toResponse(customer)

        then:
        response.employmentStatus == EmploymentStatus.EMPLOYED
        response.employerName     == "Acme Corp"
        response.annualIncome     == 95000.00
    }

    def "toEntity maps all top-level fields from CreateCustomerRequest"() {
        given:
        def request = CreateCustomerRequest.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .phone("+15555550100")
                .dateOfBirth(LocalDate.of(1985, 3, 15))
                .ssn("123-45-6789")
                .address(AddressRequest.builder()
                        .street("100 Main St").city("Austin").state("TX").zipCode("78701")
                        .build())
                .employmentDetails(EmploymentDetailsRequest.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .employerName("Acme Corp")
                        .annualIncome(95000.00)
                        .build())
                .build()

        when:
        def entity = mapper.toEntity(request)

        then:
        entity.firstName                            == "Jane"
        entity.lastName                             == "Doe"
        entity.email                                == "jane.doe@example.com"
        entity.ssn                                  == "123-45-6789"
        entity.address.street                       == "100 Main St"
        entity.employmentDetails.employmentStatus   == EmploymentStatus.EMPLOYED
        entity.id                                   != null
    }

    def "updateEntity ignores null fields, preserving existing values"() {
        given:
        def entity = customerWithSsn("123-45-6789")
        entity.firstName = "Jane"
        entity.lastName  = "Doe"

        def request = UpdateCustomerRequest.builder()
                .lastName("Smith")
                .build()

        when:
        mapper.updateEntity(request, entity)

        then:
        entity.firstName == "Jane"
        entity.lastName  == "Smith"
    }

    def "updateEntity does not overwrite SSN or dateOfBirth"() {
        given:
        def entity = customerWithSsn("123-45-6789")
        entity.dateOfBirth = LocalDate.of(1985, 3, 15)

        def request = UpdateCustomerRequest.builder().firstName("Updated").build()

        when:
        mapper.updateEntity(request, entity)

        then:
        entity.ssn         == "123-45-6789"
        entity.dateOfBirth == LocalDate.of(1985, 3, 15)
    }

    private static Customer customerWithSsn(String ssn) {
        def address = new Address(
                street: "100 Main St", city: "Austin", state: "TX", zipCode: "78701"
        )
        def employment = new EmploymentDetails(
                employmentStatus: EmploymentStatus.EMPLOYED,
                employerName: "Acme Corp",
                annualIncome: 95000.00
        )
        new Customer(
                firstName: "Jane",
                lastName: "Doe",
                email: "jane.doe@example.com",
                phone: "+15555550100",
                dateOfBirth: LocalDate.of(1985, 3, 15),
                ssn: ssn,
                address: address,
                employmentDetails: employment
        )
    }
}
