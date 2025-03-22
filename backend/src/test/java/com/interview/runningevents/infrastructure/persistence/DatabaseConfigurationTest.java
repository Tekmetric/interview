package com.interview.runningevents.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("test")
public class DatabaseConfigurationTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("classpath:database/data.sql")
    private Resource sqlScript;

    @BeforeEach
    void setup() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, sqlScript);
        } catch (Exception e) {
            System.err.println("Error executing SQL script: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void databaseConnectionTest() throws SQLException {
        assertThat(dataSource).isNotNull();
        assertThat(dataSource.getConnection()).isNotNull();
    }

    @Test
    void tableExistsTest() throws SQLException {
        DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
        ResultSet tables = metaData.getTables(null, null, "RUNNING_EVENT", null);

        assertThat(tables.next()).isTrue();
    }

    @Test
    void tableColumnsTest() throws SQLException {
        DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
        ResultSet columns = metaData.getColumns(null, null, "RUNNING_EVENT", null);

        List<String> columnNames = new ArrayList<>();
        while (columns.next()) {
            columnNames.add(columns.getString("COLUMN_NAME"));
        }

        assertThat(columnNames)
                .containsExactlyInAnyOrder("ID", "NAME", "DATE_TIME", "LOCATION", "DESCRIPTION", "FURTHER_INFORMATION");
    }

    @Test
    void indexExistsTest() throws SQLException {
        DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
        ResultSet indexes = metaData.getIndexInfo(null, null, "RUNNING_EVENT", false, false);

        List<String> indexNames = new ArrayList<>();
        while (indexes.next()) {
            indexNames.add(indexes.getString("INDEX_NAME"));
        }

        assertThat(indexNames).contains("IDX_RUNNING_EVENT_DATE_TIME");
    }

    @Test
    void dataSampleTest() {
        try {
            List<Map<String, Object>> events = jdbcTemplate.queryForList("SELECT * FROM running_event");

            assertThat(events).hasSize(3);

            // Verify first event
            Map<String, Object> firstEvent = events.get(0);
            assertThat(firstEvent.get("NAME")).isEqualTo("Spring Marathon 2025");
            assertThat(firstEvent.get("LOCATION")).isEqualTo("Central Park, New York");

            // Check that all events have dates
            assertThat(events)
                    .allSatisfy(event -> assertThat(event.get("DATE_TIME")).isNotNull());
        } catch (Exception e) {
            System.err.println("SQL Error in dataSampleTest: " + e.getMessage());

            // Let's check if the table exists at all
            Integer tableCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'RUNNING_EVENT'", Integer.class);

            System.err.println("Table exists check: " + (tableCount != null && tableCount > 0));

            throw e;
        }
    }

    @Test
    void verifyTableStructureWithSql() {
        // Direct verification using SQL to list table columns
        List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'RUNNING_EVENT'");

        List<String> columnNames = new ArrayList<>();
        for (Map<String, Object> column : columns) {
            columnNames.add(((String) column.get("COLUMN_NAME")).toUpperCase());
        }

        assertThat(columnNames)
                .containsExactlyInAnyOrder("ID", "NAME", "DATE_TIME", "LOCATION", "DESCRIPTION", "FURTHER_INFORMATION");
    }
}
