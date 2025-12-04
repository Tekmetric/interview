package com.interview.readiness;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component("db")
public class DbHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    public DbHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try (Connection c = dataSource.getConnection()) {
            if (c.isValid(1)) {
                return Health.up().withDetail("database", "reachable").build();
            } else {
                return Health.down().withDetail("database", "not valid").build();
            }
        } catch (SQLException ex) {
            return Health.down(ex).build();
        }
    }
}
