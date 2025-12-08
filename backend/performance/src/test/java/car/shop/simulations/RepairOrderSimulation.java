package car.shop.simulations;


import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class RepairOrderSimulation extends Simulation {
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .contentTypeHeader("application/json");


    String carRepairOrderBody = """
            {
                "vin": "%s",
                "carModel": "%s",
                "issueDescription": "%s"
            }
            """;

    Random random = new Random();

    String[] carModels = {"Audi A3", "BMW 320i", "Tesla Model 3", "Mercedes C200"};
    String[] issues = {"Car does not start", "Engine noise", "Brake failure", "Battery dead"};

    Iterator<Map<String, Object>> randomFeeder = new Iterator<>() {
        private char[] vinChars = "ABCDEFGHJKLMNPRSTUVWXYZ0123456789".toCharArray();

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public Map<String, Object> next() {
            StringBuilder vin = new StringBuilder(17);
            for (int i = 0; i < 17; i++) {
                vin.append(vinChars[random.nextInt(vinChars.length)]);
            }

            return Map.of(
                    "vin", vin.toString(),
                    "carModel", carModels[random.nextInt(carModels.length)],
                    "issueDescription", issues[random.nextInt(issues.length)]
            );
        }
    };

    ScenarioBuilder scn = scenario("Create Repair Order Scenario")
            .feed(randomFeeder)
            .exec(http("Create Repair Order")
                    .post("/api/v1/repair-orders")
                    .body(StringBody(session -> carRepairOrderBody
                            .formatted(
                                    session.getString("vin"),
                                    session.getString("carModel"),
                                    session.getString("issueDescription")
                            )))
                    .check(status().is(201))
            );

    {
        setUp(
                scn.injectOpen(
                        atOnceUsers(60),
                        rampUsers(500).during(10)
                )
        ).protocols(httpProtocol);
    }
}
