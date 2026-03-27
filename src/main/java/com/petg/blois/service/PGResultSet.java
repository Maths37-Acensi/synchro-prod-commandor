package com.petg.blois.service;

import lombok.Data;

import java.sql.*;

/**
 * MaifResultSet
 *
 * @author F. LUTZ (97211P)
 */
@Data
public class PGResultSet implements AutoCloseable {
    private final Connection connection;
    private final Statement statement;
    private final ResultSet resultSet;

    public PGResultSet(Connection connection, PreparedStatement statement) throws SQLException {
        this.connection = connection;
        this.statement = statement;
        try {
            this.resultSet = statement.executeQuery();
        } catch (SQLException e) {
            statement.close();
            connection.close();
            throw e;
        }
    }

    @Override
    public void close() throws SQLException {
        try (this.resultSet) {
            // NOSONAR
        }
        try (this.statement) {
            // NOSONAR
        }
        try (this.connection) {
            // NOSONAR
        }
    }
}

