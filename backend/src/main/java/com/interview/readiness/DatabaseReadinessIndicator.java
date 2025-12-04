package com.interview.readiness;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DatabaseReadinessIndicator extends AbstractHealthIndicator {

    private final DataSource dataSource;

    public DatabaseReadinessIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        try (Connection c = dataSource.getConnection()) {
            if (c.isValid(1)) {
                builder.up();
            } else {
                builder.down();
            }
        } catch (SQLException ex) {
            builder.down(ex);
        }
    }
}