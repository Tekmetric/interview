package com.interview.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import org.h2.tools.TriggerAdapter;

import com.interview.dto.DataPayload;

/**
 * Spring with H2 in-memory doesn't handle database-generated column values gracefully.
 * I played around with a few different ways to get it to work, this was one of those
 * explorations, although it's not currently used.
 *
 * @see com.interview.resource.CrudDataResource#dataCreateOne(DataPayload)
 * @see com.interview.resource.CrudDataResource#dataUpdateOne(UUID, DataPayload)
 */
public class UpdatedTrigger extends TriggerAdapter {

    @Override
    public void fire(Connection conn, ResultSet oldRow, ResultSet newRow) throws SQLException {
        newRow.updateTimestamp("updated", Timestamp.from(Instant.now()));
    }

}
