package com.interview.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.interview.vehicle.model.VehicleType
import com.interview.vehicle.web.model.CreateVehicleRequest
import com.interview.vehicle.web.model.UpdateVehicleRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class VehicleControllerIT extends Specification {

    @Autowired
    MockMvc mockMvc

    @Autowired
    ObjectMapper objectMapper

    def "Test GET fetch vehicles"() {
        when:
        def result = mockMvc.perform(get("/api/vehicles")
                .param("page", "0")
                .param("size", "2"))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath("\$.totalElements").value(5))
                .andExpect(jsonPath("\$.numberOfElements").value(2))
                .andExpect(jsonPath("\$.totalPages").value(3))
                .andExpect(jsonPath("\$.content").exists())
                .andExpect(jsonPath("\$.content").isArray())
                .andExpect(jsonPath("\$.content.length()").value(2))
                .andExpect(jsonPath("\$.content[0].id").value(1))
                .andExpect(jsonPath("\$.content[0].type").value("SEDAN"))
                .andExpect(jsonPath("\$.content[0].fabricationYear").value(2016))
                .andExpect(jsonPath("\$.content[0].make").value("Toyota"))
                .andExpect(jsonPath("\$.content[0].model").value("Prius"))
                .andExpect(jsonPath("\$.content[1].id").value(2))
                .andExpect(jsonPath("\$.content[1].type").value("SUV"))
                .andExpect(jsonPath("\$.content[1].fabricationYear").value(2022))
                .andExpect(jsonPath("\$.content[1].make").value("Ford"))
                .andExpect(jsonPath("\$.content[1].model").value("Ranger"))
    }

    def "Test GET fetch vehicles - large page size"() {
        when:
        def result = mockMvc.perform(get("/api/vehicles")
                .param("page", "0")
                .param("size", "100000"))

        then:
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("\$.status").value(400))
                .andExpect(jsonPath("\$.title").value("Bad Request"))
                .andExpect(jsonPath("\$.detail").value("Page size cannot be greater than 500"))
                .andExpect(jsonPath("\$.timestamp").exists())

    }

    def "Test GET fetch vehicle by id: #id"() {
        when:
        def result = mockMvc.perform(get("/api/vehicles/${id}"))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath("\$.id").value(id))
                .andExpect(jsonPath("\$.type").value(type))
                .andExpect(jsonPath("\$.fabricationYear").value(fabricationYear))
                .andExpect(jsonPath("\$.make").value(make))
                .andExpect(jsonPath("\$.model").value(model))


        where:
        id || type                         | fabricationYear | make     | model
        1  || VehicleType.SEDAN.toString() | 2016            | "Toyota" | "Prius"
        2  || VehicleType.SUV.toString()   | 2022            | "Ford"   | "Ranger"
    }

    def "Test GET fetch vehicle by id: #id - not found"() {
        given:
        def id = 200 //not existing id

        when:
        def result = mockMvc.perform(get("/api/vehicles/${id}"))

        then:
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("\$.status").value(404))
                .andExpect(jsonPath("\$.title").value("Not Found"))
                .andExpect(jsonPath("\$.detail").value("Could not find vehicle with id: 200"))
                .andExpect(jsonPath("\$.timestamp").exists())
                .andExpect(jsonPath("\$.timestamp").isNotEmpty())
    }

    def "Test POST create new vehicle"() {
        given:
        def request = CreateVehicleRequest.builder()
                .type(VehicleType.SEDAN)
                .fabricationYear(2023)
                .make("Audi")
                .model("A6")
                .build()

        when:
        def result = mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then:
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("\$.id").exists())
                .andExpect(jsonPath("\$.type").value("SEDAN"))
                .andExpect(jsonPath("\$.fabricationYear").value(2023))
                .andExpect(jsonPath("\$.make").value("Audi"))
                .andExpect(jsonPath("\$.model").value("A6"))
    }

    def "Test POST create new vehicle - invalid request"() {
        given:
        def request = CreateVehicleRequest.builder()
                .fabricationYear(1899)
                .build()

        when:
        def result = mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then:
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("\$.status").value(400))
                .andExpect(jsonPath("\$.title").value("Bad Request"))
                .andExpect(jsonPath("\$.timestamp").exists())
                .andExpect(jsonPath("\$.errors").exists())
                .andExpect(jsonPath("\$.errors").isArray())
                .andExpect(jsonPath("\$.errors.length()").value(4))
                .andExpect(jsonPath("\$.errors[0].field").value("fabricationYear"))
                .andExpect(jsonPath("\$.errors[0].value").value("1899"))
                .andExpect(jsonPath("\$.errors[0].details").value("Year of fabrication should not be older than 1900"))
                .andExpect(jsonPath("\$.errors[1].field").value("make"))
                .andExpect(jsonPath("\$.errors[1].value").doesNotExist())
                .andExpect(jsonPath("\$.errors[1].details").value("make is required"))
                .andExpect(jsonPath("\$.errors[2].field").value("model"))
                .andExpect(jsonPath("\$.errors[2].value").doesNotExist())
                .andExpect(jsonPath("\$.errors[2].details").value("model is required"))
                .andExpect(jsonPath("\$.errors[3].field").value("type"))
                .andExpect(jsonPath("\$.errors[3].value").doesNotExist())
                .andExpect(jsonPath("\$.errors[3].details").value("type is required"))
    }

    def "Test PUT update vehicle by id"() {
        given:
        def id = 2
        def request = UpdateVehicleRequest.builder()
                .type(VehicleType.SEDAN)
                .fabricationYear(2023)
                .make("Audi")
                .model("A6")
                .build()

        when:
        def result = mockMvc.perform(put("/api/vehicles/${id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath("\$.id").exists())
                .andExpect(jsonPath("\$.type").value("SEDAN"))
                .andExpect(jsonPath("\$.fabricationYear").value(2023))
                .andExpect(jsonPath("\$.make").value("Audi"))
                .andExpect(jsonPath("\$.model").value("A6"))
    }

    def "Test PUT update vehicle by id - invalid request"() {
        given:
        def id = 2
        def request = UpdateVehicleRequest.builder()
                .fabricationYear(1899)
                .build()

        when:
        def result = mockMvc.perform(put("/api/vehicles/${id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then:
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("\$.status").value(400))
                .andExpect(jsonPath("\$.title").value("Bad Request"))
                .andExpect(jsonPath("\$.timestamp").exists())
                .andExpect(jsonPath("\$.errors").exists())
                .andExpect(jsonPath("\$.errors").isArray())
                .andExpect(jsonPath("\$.errors.length()").value(4))
                .andExpect(jsonPath("\$.errors[0].field").value("fabricationYear"))
                .andExpect(jsonPath("\$.errors[0].value").value("1899"))
                .andExpect(jsonPath("\$.errors[0].details").value("Year of fabrication should not be older than 1900"))
                .andExpect(jsonPath("\$.errors[1].field").value("make"))
                .andExpect(jsonPath("\$.errors[1].value").doesNotExist())
                .andExpect(jsonPath("\$.errors[1].details").value("make is required"))
                .andExpect(jsonPath("\$.errors[2].field").value("model"))
                .andExpect(jsonPath("\$.errors[2].value").doesNotExist())
                .andExpect(jsonPath("\$.errors[2].details").value("model is required"))
                .andExpect(jsonPath("\$.errors[3].field").value("type"))
                .andExpect(jsonPath("\$.errors[3].value").doesNotExist())
                .andExpect(jsonPath("\$.errors[3].details").value("type is required"))
    }

    def "Test DELETE remove vehicle by id"() {
        given:
        def id = 3

        when:
        def result = mockMvc.perform(delete("/api/vehicles/${id}"))

        then:
        result.andExpect(status().isNoContent())

        mockMvc.perform(get("/api/vehicles/${id}")).andExpect(status().isNotFound())
    }
}
