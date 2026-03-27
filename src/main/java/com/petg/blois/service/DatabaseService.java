package com.petg.blois.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.petg.blois.data.SqlColumn;
import com.petg.blois.data.SqlDdl;
import com.petg.blois.exception.ServiceException;
import com.petg.blois.util.DatabaseUtils;
import com.petg.blois.util.PGUtils;
import com.petg.blois.util.ReflectionPropertyDescriptor;
import com.petg.blois.util.ReflectionUtils;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseService {
    public static final int DEFAULT_FETCH_SIZE = 1000;
    private static String database;
    private final HikariDataSource dataSource;
    private final ResultSetService resultSetService;
    private int fetchSize = DEFAULT_FETCH_SIZE;

    public String getDatabase() {
        if (StringUtils.isEmpty(database)) {
            try (Connection connection = dataSource.getConnection()) {
                database = StringUtils.isEmpty(connection.getCatalog()) ? connection.getSchema() : connection.getCatalog();
            } catch (SQLException e) {
                throw new ServiceException("Can't get database name.", e);
            }
        }

        return database;
    }

    /**
     * Permet d'adapter le nombre d'éléments retournés dans un lot.
     *
     * @param fetchSize Valeur à renseigner
     * @return Connecteur
     */
    public DatabaseService withFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
        return this;
    }

    /**
     * Used data source connection test query (this query must be informed !).
     * Execute this query.
     *
     * @return true if no errors, false otherwise.
     */
    public boolean checkConnection() {
        String testQuery = dataSource.getConnectionTestQuery();
        boolean select = testQuery.trim().toLowerCase().startsWith("select");
        boolean checkOk;

        try {
            if (select) {
                checkOk = findFirst(testQuery, Object.class).isPresent();
            } else {
                checkOk = executeUpdate(testQuery) != 0;
            }
        } catch (Exception e) {
            log.warn("Test connection fail !", e);
            checkOk = false;
        }

        log.debug(checkOk ? "Query '{}' executed successfully." : "Query '{}' failed.", testQuery);

        return checkOk;
    }

    /**
     * You must close the ResultSet at the end
     *
     * @param query : query to execute
     * @return ResultSet with the result of the query
     */
    public PGResultSet executeQuery(String query) {
        log.trace("{} : {}", database, query);
        try {
            Connection connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setFetchSize(fetchSize);
            return new PGResultSet(connection, stmt);
        } catch (Exception e) {
            throw new ServiceException(String.format("%s : %s", database, query), e);
        }
    }

    /**
     * Execute query that not return results (like update, insert into, alter...)
     *
     * @param query : query to execute
     * @return Number of lines updated
     */
    public int executeUpdate(String query) {
        log.trace("{} : {}", database, query);
        try (Connection connection = dataSource.getConnection();
             Statement st = connection.createStatement()) {
            return st.executeUpdate(query);
        } catch (SQLException e) {
            throw new ServiceException(String.format("%s : %s", database, query), e);
        }
    }


    /**
     * Get a single clazz objets that contains results of query
     * T must be default Java class (int, Long, String, Date...) or object that contains attribute of default Java class
     *
     * @param query : query to execute
     * @param clazz : Object that represents query results
     * @param <T>   : Type of clazz
     * @return T
     */
    public <T> Optional<T> findFirst(String query, Class<T> clazz) {
        List<T> results = findAll(query, clazz, true);
        return !results.isEmpty() ? Optional.ofNullable(results.getFirst()) : Optional.empty();
    }

    /**
     * Get a list of clazz objets that contains results of query
     * T must be default Java class (int, Long, String, Date...) or object that contains attribute of default Java class
     *
     * @param query           query to execute
     * @param clazz           object that represents query results
     * @param onlyFirstRowOpt if true, return only the first row, otherwise return all rows
     * @param <T>             Type of clazz
     * @return list of T
     */
    public <T> List<T> findAll(String query, Class<T> clazz, Boolean... onlyFirstRowOpt) {
        boolean onlyFirstRow = PGUtils.findFirstParameter(onlyFirstRowOpt)
                .orElse(false);
        try (PGResultSet mrs = executeQuery(query)) {
            return resultSetService.mapToObjects(mrs.getResultSet(), clazz, onlyFirstRow);
        } catch (SQLException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Renvoi la liste des colonnes qui matchent avec les filtres appliqués
     *
     * @return Liste des colonnes
     */
    public List<SqlColumn> findAllColumns(String schemaName, String tableName) {
        String query = String.format("""
                        SELECT
                          LOWER(TABLE_SCHEMA) schema_name,
                          LOWER(TABLE_NAME) table_name,
                          ORDINAL_POSITION position,
                          LOWER(COLUMN_NAME) column_name,
                          LOWER(DATA_TYPE) data_type,
                          CASE
                              WHEN DATA_TYPE = 'nvarchar' THEN CONCAT(DATA_TYPE, '(', CHARACTER_MAXIMUM_LENGTH, ')')
                              WHEN DATA_TYPE = 'numeric' THEN CONCAT(DATA_TYPE, '(', NUMERIC_PRECISION, ', ', NUMERIC_SCALE, ')')
                              ELSE DATA_TYPE
                          END ddl_type,
                          CASE
                              WHEN IS_NULLABLE = 'NO' THEN CAST(0 AS bit)
                              ELSE CAST(1 AS bit)
                          END nullable
                        FROM information_schema.columns
                        WHERE TABLE_SCHEMA = '%s' AND TABLE_NAME = '%s'
                        ORDER BY 1, 2, 3""",
                schemaName,
                tableName
        );

        return findAll(query, SqlColumn.class);
    }

    /**
     * Renvoi la définition de la vue
     *
     * @param schemaName schéma
     * @param viewName   vue
     * @return Définition de la vue si elle existe
     */
    public Optional<String> findDefinitionView(String schemaName, String viewName) {
        String query = String.format("""
                        SELECT m.definition
                        FROM sys.sql_modules m
                        WHERE m.object_id = object_id('%s.%s', 'V')""",
                schemaName,
                viewName
        );

        return findFirst(query, String.class);
    }

    /**
     * Save an object in Greenplum database.
     *
     * @param schemaName schema name
     * @param tableName  table name
     * @param t          object to save
     * @param <T>        generic type to save
     * @return number of saved lines
     */
    public <T> int save(String schemaName, String tableName, T t) {
        return saveAll(schemaName, tableName, Collections.singletonList(t));
    }

    /**
     * Save objects in Greenplum database.
     *
     * @param schemaName schema name
     * @param tableName  table name
     * @param collection objects to save
     * @param <T>        generic type to save
     * @return number of saved lines
     */
    @SuppressWarnings("unchecked")
    public <T> int saveAll(String schemaName, String tableName, Collection<T> collection) {
        if (collection.isEmpty()) {
            return 0;
        }
        Class<T> clazz = (Class<T>) collection.stream()
                .findFirst()
                .get()
                .getClass();
        List<SqlColumn> columns = findAllColumns(schemaName, tableName).stream()
                .sorted(Comparator.comparingInt(SqlColumn::getPosition))
                .toList();
        List<ReflectionPropertyDescriptor> descriptors = ReflectionUtils.findAllPropertyDescriptors(clazz);
        List<Pair<SqlColumn, ReflectionPropertyDescriptor>> columnAndDescriptors = columns.stream()
                .map(column -> {
                    ReflectionPropertyDescriptor descriptor = descriptors.stream()
                            .filter(d -> !d.getField().isAnnotationPresent(JsonIgnore.class))
                            .filter(d -> PGUtils.snakeToCamelCase(column.getColumnName(), false).equalsIgnoreCase(d.getField().getName()))
                            .findFirst()
                            .orElse(null);
                    return Pair.of(column, descriptor);
                })
                .filter(elt -> elt.getRight() != null)
                .toList();
        return ListUtils.partition(new ArrayList<>(collection), 1000)
                .stream()
                .map(list -> saveAllSub(schemaName, tableName, list, columnAndDescriptors))
                .reduce(Integer::sum)
                .orElse(0);
    }

    private <T> int saveAllSub(String schemaName, String tableName, List<T> collection, List<Pair<SqlColumn, ReflectionPropertyDescriptor>> columnAndDescriptors) {
        String values = collection.stream()
                .map(t -> columnAndDescriptors.stream()
                        .map(cd -> ReflectionUtils.readField(cd.getRight(), t)
                                .orElse(null)
                        )
                        .map(DatabaseUtils::javaToSql)
                        .reduce((v1, v2) -> String.format("%s, %s", v1, v2))
                        .orElseThrow(() -> new IllegalArgumentException("No columns match")))
                .map(value -> String.format("(%s)", value))
                .reduce((v1, v2) -> String.format("%s, %s", v1, v2))
                .orElseThrow(() -> new ServiceException("No data !"));

        String query = String.format("""
                        INSERT INTO %s.%s
                            (%s)
                        VALUES %s
                        """,
                schemaName,
                tableName,
                DatabaseUtils.generateColumnsDdl(columnAndDescriptors.stream().map(Pair::getKey).toList(), SqlDdl.NAME),
                values
        );

        return executeUpdate(query);
    }
}
