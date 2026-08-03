package com.interview.simulation;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.Simulation;
import java.util.UUID;

public class CreateRepairOrderSimulation extends Simulation {

  private static final String BASE_URL =
      System.getProperty("baseUrl", "http://localhost:8080");

  private static final String CUSTOMER_ID = "01966c3a-0000-7000-8000-000000000001";

  {
    var httpProtocol = http
        .baseUrl(BASE_URL)
        .acceptHeader("application/json")
        .contentTypeHeader("application/json");

    var createOrder = scenario("Create Repair Order")
        .exec(session -> session.set("customerId", CUSTOMER_ID))
        .exec(
            http("POST /api/v1/repair-orders")
                .post("/api/v1/repair-orders")
                .body(StringBody(session -> """
                    {
                      "description": "Load test order %s",
                      "vehicleMake": "Toyota",
                      "vehicleModel": "Camry",
                      "vehicleYear": 2023,
                      "licensePlate": "LT-%s",
                      "customerId": "%s",
                      "lineItems": [
                        {
                          "description": "Oil change",
                          "unitPrice": 49.99
                        },
                        {
                          "description": "Filter replacement",
                          "unitPrice": 15.00
                        }
                      ]
                    }
                    """.formatted(
                    UUID.randomUUID(),
                    UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    session.getString("customerId"))))
                .check(status().is(201))
        );

    setUp(
        createOrder.injectOpen(
            rampUsersPerSec(10).to(100).during(100),
            constantUsersPerSec(10).during(20)
        )
    ).protocols(httpProtocol)
        .assertions(
            global().responseTime().percentile3().lt(500),
            global().successfulRequests().percent().gt(99.0)
        );
  }
}
