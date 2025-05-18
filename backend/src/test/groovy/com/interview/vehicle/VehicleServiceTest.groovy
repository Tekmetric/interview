package com.interview.vehicle

import com.interview.vehicle.exception.VehicleNotFoundException
import com.interview.vehicle.model.*
import com.interview.vehicle.persistence.entity.VehicleEntity
import com.interview.vehicle.persistence.respository.VehicleRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import spock.lang.Specification

import java.time.Year

class VehicleServiceTest extends Specification {

    private VehicleRepository vehicleRepository

    private VehicleService vehicleService

    void setup() {
        vehicleRepository = Mock()
        vehicleService = new VehicleService(vehicleRepository)
    }

    def "Test get vehicle"() {
        given:
        VehicleId vehicleId = VehicleId.fromValue(1)

        when:
        Vehicle result = vehicleService.get(vehicleId)

        then:
        1 * vehicleRepository.findById(vehicleId) >> Optional.ofNullable(Mock(VehicleEntity))

        expect:
        result != null
    }

    def "Test get vehicle - not found"() {
        given:
        VehicleId vehicleId = VehicleId.fromValue(1)

        when:
        vehicleService.get(vehicleId)

        then:
        1 * vehicleRepository.findById(vehicleId) >> Optional.empty()

        thrown VehicleNotFoundException
    }

    def "Test get vehicles"() {
        given:
        Pageable pageable = PageRequest.of(0, 10)

        when:
        Page<Vehicle> result = vehicleService.getAll(pageable)

        then:
        1 * vehicleRepository.findAll(pageable) >> new PageImpl<>(vehicles, pageable, vehicles.size())

        expect:
        result != null
        result.totalElements == 2
        result.totalPages == 1
        result.content != null
        result.content == vehicles

        where:
        vehicles = [new VehicleEntity(), new VehicleEntity()]
    }

    def "Test create vehicle"() {
        given:
        VehicleCreate create = VehicleCreate.builder()
                .type(newType)
                .fabricationYear(newFabricationYear)
                .make(newBrand)
                .model(newModel)
                .build()

        when:
        Vehicle result = vehicleService.create(create)

        then:
        1 * vehicleRepository.save(_) >> { VehicleEntity v -> v }

        expect:
        result != null
        result.fabricationYear() == newFabricationYear
        result.type() == newType
        result.make() == newBrand
        result.model() == newModel

        where:
        newFabricationYear = Year.of(2023)
        newType = VehicleType.SEDAN
        newBrand = "Audi"
        newModel = "A4"

    }

    def "Test update vehicle"() {
        given:
        VehicleId vehicleId = VehicleId.fromValue(1)
        VehicleUpdate update = VehicleUpdate.builder()
                .type(newType)
                .fabricationYear(newFabricationYear)
                .make(newBrand)
                .model(newModel)
                .build()

        when:
        Vehicle result = vehicleService.update(vehicleId, update)

        then:
        1 * vehicleRepository.findById(vehicleId) >> Optional.of(new VehicleEntity())
        1 * vehicleRepository.save(_) >> { VehicleEntity v -> v }

        expect:
        result != null
        result.fabricationYear() == newFabricationYear
        result.type() == newType
        result.make() == newBrand
        result.model() == newModel

        where:
        newFabricationYear = Year.of(2023)
        newType = VehicleType.SEDAN
        newBrand = "Audi"
        newModel = "A4"
    }

    def "Test delete vehicle by id"() {
        given:
        VehicleId vehicleId = VehicleId.fromValue(1)

        when:
        vehicleService.delete(vehicleId)

        then:
        1 * vehicleRepository.deleteById(vehicleId)
    }
}
