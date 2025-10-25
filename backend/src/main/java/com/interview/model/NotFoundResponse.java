package com.interview.model;

/**
 * Class to hold the table name for responses for row not found in table.
 */
public class NotFoundResponse {
    private final String table;

    /**
     * Constructor
     *
     * @param table the table that is missing the response.
     */
    public NotFoundResponse(String table) {
        this.table = table;
    }

    /**
     * The response for api calls that do not return any rows
     *
     * @return The return string for row not found in table.
     */
    public String getResponse(Exception e) {
        if (e.getMessage() != null && !e.getMessage().isEmpty()) {
            return "Table: " + table + ". Error: " + e.getMessage();
        }
        return "Row not found for table: " + table;
    }
}
