package com.interview.gatling;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class WidgetSimulation extends Simulation {

    // Feeder to generate random widget data
    Iterator<Map<String, Object>> widgetFeeder = Stream.generate((Supplier<Map<String, Object>>) () -> {
        int randomNum = ThreadLocalRandom.current().nextInt(1, 10000);
        return Map.of(
            "name", "Widget-" + randomNum,
            "description", "Description for widget " + randomNum
        );
    }).iterator();

    // Feeder to generate widget IDs for read/update/delete operations
    // This creates a circular feeder with IDs 1-1000
    Iterator<Map<String, Object>> widgetIdFeeder = Stream.generate((Supplier<Map<String, Object>>) () -> {
        int id = ThreadLocalRandom.current().nextInt(1, 1001);
        return Map.of("widgetId", id);
    }).iterator();

    // HTTP protocol configuration
    HttpProtocolBuilder httpProtocol = http
        .baseUrl("http://localhost:8080")
        .acceptHeader("application/json")
        .contentTypeHeader("application/json");

    // Scenario 1: Create widgets
    ScenarioBuilder createWidgets = scenario("Create Widgets")
        .feed(widgetFeeder)
        .exec(
            http("Create Widget")
                .post("/api/widgets")
                .body(StringBody("""
                    {
                        "name": "#{name}",
                        "description": "#{description}"
                    }
                    """))
                .check(status().is(201))
                .check(jsonPath("$.id").saveAs("widgetId"))
        )
        .pause(Duration.ofMillis(100));

    // Scenario 2: Get all widgets (tests cache)
    ScenarioBuilder getAllWidgets = scenario("Get All Widgets")
        .exec(
            http("Get All Widgets")
                .get("/api/widgets")
                .check(status().is(200))
                .check(jsonPath("$[*]").exists())
        )
        .pause(Duration.ofMillis(100));

    // Scenario 3: Get widget by ID (tests cache)
    ScenarioBuilder getWidgetById = scenario("Get Widget By ID")
        .feed(widgetIdFeeder)
        .exec(
            http("Get Widget By ID")
                .get("/api/widgets/#{widgetId}")
                .check(status().in(200, 404))
        )
        .pause(Duration.ofMillis(100));

    // Scenario 4: Update widgets
    ScenarioBuilder updateWidgets = scenario("Update Widgets")
        .feed(widgetIdFeeder)
        .feed(widgetFeeder)
        .exec(
            http("Update Widget")
                .put("/api/widgets/#{widgetId}")
                .body(StringBody("""
                    {
                        "name": "#{name}",
                        "description": "Updated: #{description}"
                    }
                    """))
                .check(status().in(200, 404, 409))
        )
        .pause(Duration.ofMillis(100));

    // Scenario 5: Delete widgets
    ScenarioBuilder deleteWidgets = scenario("Delete Widgets")
        .feed(widgetIdFeeder)
        .exec(
            http("Delete Widget")
                .delete("/api/widgets/#{widgetId}")
                .check(status().in(204, 404))
        )
        .pause(Duration.ofMillis(100));

    // Scenario 6: Mixed operations (realistic workflow)
    ScenarioBuilder mixedOperations = scenario("Mixed Operations")
        .feed(widgetFeeder)
        .exec(
            // Create a widget
            http("Create Widget")
                .post("/api/widgets")
                .body(StringBody("""
                    {
                        "name": "#{name}",
                        "description": "#{description}"
                    }
                    """))
                .check(status().is(201))
                .check(jsonPath("$.id").saveAs("newWidgetId"))
        )
        .pause(Duration.ofMillis(200))
        .exec(
            // Get all widgets (cache hit after first request)
            http("Get All Widgets")
                .get("/api/widgets")
                .check(status().is(200))
        )
        .pause(Duration.ofMillis(200))
        .exec(
            // Get the specific widget we just created (cache hit after first request)
            http("Get Widget By ID")
                .get("/api/widgets/#{newWidgetId}")
                .check(status().is(200))
                .check(jsonPath("$.name").is("#{name}"))
        )
        .pause(Duration.ofMillis(200))
        .feed(widgetFeeder)
        .exec(
            // Update the widget (invalidates cache)
            http("Update Widget")
                .put("/api/widgets/#{newWidgetId}")
                .body(StringBody("""
                    {
                        "name": "#{name}",
                        "description": "Updated: #{description}"
                    }
                    """))
                .check(status().is(200))
        )
        .pause(Duration.ofMillis(200))
        .exec(
            // Get the widget again (cache miss after update)
            http("Get Widget After Update")
                .get("/api/widgets/#{newWidgetId}")
                .check(status().is(200))
        )
        .pause(Duration.ofMillis(200))
        .exec(
            // Delete the widget (invalidates cache)
            http("Delete Widget")
                .delete("/api/widgets/#{newWidgetId}")
                .check(status().is(204))
        )
        .pause(Duration.ofMillis(200));

    {
        setUp(
            // Run different scenarios with different load patterns
            createWidgets.injectOpen(
                rampUsersPerSec(1).to(10).during(Duration.ofSeconds(30)),
                constantUsersPerSec(10).during(Duration.ofSeconds(60))
            ),
            getAllWidgets.injectOpen(
                rampUsersPerSec(1).to(20).during(Duration.ofSeconds(30)),
                constantUsersPerSec(20).during(Duration.ofSeconds(60))
            ),
            getWidgetById.injectOpen(
                rampUsersPerSec(1).to(15).during(Duration.ofSeconds(30)),
                constantUsersPerSec(15).during(Duration.ofSeconds(60))
            ).andThen(
                updateWidgets.injectOpen(
                    rampUsersPerSec(1).to(5).during(Duration.ofSeconds(30)),
                    constantUsersPerSec(5).during(Duration.ofSeconds(60))
                )
            ).andThen(
                deleteWidgets.injectOpen(
                    rampUsersPerSec(1).to(3).during(Duration.ofSeconds(30)),
                    constantUsersPerSec(3).during(Duration.ofSeconds(60))
                )
            ),
            mixedOperations.injectOpen(
                rampUsersPerSec(1).to(5).during(Duration.ofSeconds(30)),
                constantUsersPerSec(5).during(Duration.ofSeconds(90))
            )
        ).protocols(httpProtocol);
    }
}
