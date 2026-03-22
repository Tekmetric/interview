package com.interview.mapper

import java.time.LocalDate
import java.time.Instant

import org.mapstruct.factory.Mappers
import spock.lang.Specification
import spock.lang.Subject

import com.interview.persistence.entity.CreditApplication
import com.interview.persistence.entity.Customer
import com.interview.persistence.entity.embedded.Address
import com.interview.persistence.entity.embedded.EmploymentDetails
import com.interview.persistence.enums.ApplicationStatus
import com.interview.persistence.enums.EmploymentStatus
import com.interview.persistence.enums.LoanPurpose
import com.interview.persistence.enums.SupportingDocumentType
import com.interview.dto.request.CreateCreditApplicationRequest
import com.interview.dto.request.UpdateApplicationStatusRequest
import com.interview.dto.request.embedded.SupportingDocumentRequest

class CreditApplicationMapperSpec extends Specification {

    @Subject
    CreditApplicationMapper mapper = Mappers.getMapper(CreditApplicationMapper)

    def "toResponse projects customer id and full name onto flat response fields"() {
        given:
        def app = applicationWithStatus(ApplicationStatus.SUBMITTED)

        when:
        def response = mapper.toResponse(app)

        then:
        response.customerId   == app.customer.id
        response.customerName == "Jane Doe"
    }

    def "toResponse maps all core application fields"() {
        given:
        def app = applicationWithStatus(ApplicationStatus.SUBMITTED)

        when:
        def response = mapper.toResponse(app)

        then:
        response.status               == ApplicationStatus.SUBMITTED
        response.requestedLoanAmount  == 35000.00
        response.loanPurpose          == LoanPurpose.VEHICLE_PURCHASE
        response.monthlyDebt          == 500.00
        response.notes                == "First-time buyer, stable employment for 5 years"
    }

    def "toEntity maps loan fields from request but leaves service-managed fields null"() {
        given:
        def request = CreateCreditApplicationRequest.builder()
                .customerId(UUID.randomUUID())
                .requestedLoanAmount(35000.00)
                .loanPurpose(LoanPurpose.VEHICLE_PURCHASE)
                .monthlyDebt(500.00)
                .notes("First-time buyer, stable employment for 5 years")
                .documents([SupportingDocumentRequest.builder().documentType(SupportingDocumentType.PROOF_OF_INCOME).build()])
                .build()

        when:
        def entity = mapper.toEntity(request)

        then:
        entity.requestedLoanAmount == 35000.00
        entity.loanPurpose         == LoanPurpose.VEHICLE_PURCHASE
        entity.monthlyDebt         == 500.00
        entity.notes               == "First-time buyer, stable employment for 5 years"

        and: "service-managed and lifecycle fields are untouched by the mapper"
        entity.customer    == null
        entity.status      == ApplicationStatus.SUBMITTED
        entity.submittedAt == null
        entity.id          != null
        entity.documents   == []
    }

    def "updateEntity applies new status to entity"() {
        given:
        def entity = applicationWithStatus(ApplicationStatus.SUBMITTED)
        def request = UpdateApplicationStatusRequest.builder()
                .status(ApplicationStatus.UNDER_REVIEW)
                .build()

        when:
        mapper.updateEntity(request, entity)

        then:
        entity.status == ApplicationStatus.UNDER_REVIEW
    }

    def "updateEntity does not touch loan amount, customer, or timestamps"() {
        given:
        def entity = applicationWithStatus(ApplicationStatus.SUBMITTED)
        def originalAmount     = entity.requestedLoanAmount
        def originalCustomer   = entity.customer
        def originalSubmittedAt = entity.submittedAt

        def request = UpdateApplicationStatusRequest.builder()
                .status(ApplicationStatus.UNDER_REVIEW)
                .build()

        when:
        mapper.updateEntity(request, entity)

        then:
        entity.requestedLoanAmount == originalAmount
        entity.customer            == originalCustomer
        entity.submittedAt         == originalSubmittedAt
    }

    def "toResponseList maps each element independently"() {
        given:
        def apps = [
                applicationWithStatus(ApplicationStatus.SUBMITTED),
                applicationWithStatus(ApplicationStatus.UNDER_REVIEW)
        ]

        when:
        def responses = mapper.toResponseList(apps)

        then:
        responses.size()         == 2
        responses[0].status      == ApplicationStatus.SUBMITTED
        responses[1].status      == ApplicationStatus.UNDER_REVIEW
        responses[0].customerName == "Jane Doe"
        responses[1].customerName == "Jane Doe"
    }

    private static CreditApplication applicationWithStatus(ApplicationStatus status) {
        def customer = new Customer(
                firstName: "Jane",
                lastName: "Doe",
                email: "jane.doe@example.com",
                phone: "+15555550100",
                dateOfBirth: LocalDate.of(1985, 3, 15),
                ssn: "123-45-6789",
                address: new Address(
                        street: "100 Main St", city: "Austin", state: "TX", zipCode: "78701"
                ),
                employmentDetails: new EmploymentDetails(
                        employmentStatus: EmploymentStatus.EMPLOYED,
                        employerName: "Acme Corp",
                        annualIncome: 95000.00
                )
        )

        new CreditApplication(
                customer: customer,
                status: status,
                requestedLoanAmount: 35000.00,
                loanPurpose: LoanPurpose.VEHICLE_PURCHASE,
                monthlyDebt: 500.00,
                notes: "First-time buyer, stable employment for 5 years",
                submittedAt: Instant.parse("2024-01-15T10:00:00Z")
        )
    }
}
