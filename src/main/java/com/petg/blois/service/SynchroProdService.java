package com.petg.blois.service;

import com.petg.blois.config.BatchProperties;
import com.petg.blois.config.DatabaseProperties;
import com.petg.blois.data.*;
import com.petg.blois.util.DatabaseUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static com.petg.blois.data.SynchroType.DELTA_DATE;
import static com.petg.blois.data.SynchroType.DELTA_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SynchroProdService {
    private static final String TMP = "_tmp";
    private final BatchProperties batchProperties;
    private final DatabaseProperties databaseProperties;
    private final ResourceLoader resourceLoader;
    private final DatabaseService databaseService;
    private final DatabaseProdService databaseProdService;
    private String startDateFormatted;

    @Scheduled(fixedDelayString = "60000") //this works.
    public void synchroProd() {
        startDateFormatted = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now().withNano(0));
        switch (batchProperties.key()) {
            case SynchroKey.COMMANDOR_V1 -> Arrays.stream(SynchroCommandorV1.values())
                    .forEach(sc -> syncTable(sc.name().toLowerCase(), sc.getClazz(), sc.getSynchroType(), sc.getDateColumn()));
            case SynchroKey.COMMANDOR_V2 -> Arrays.stream(SynchroCommandorV2.values())
                    .forEach(sc -> syncTable(sc.name().toLowerCase(), sc.getClazz(), sc.getSynchroType(), sc.getDateColumn()));
        }
    }

    private <T> void syncTable(String table, Class<T> clazz, SynchroType synchroType, String dateColumn) {
        log.info("-------------------------------------------------");
        log.info("Starting sync table {}", table);

        String schema = databaseProperties.schema();
        String tmpTable = table + TMP;
        List<SqlColumn> columns = databaseService.findAllColumns(schema, table);

        String truncateQuery = String.format("TRUNCATE TABLE %s.%s", schema, tmpTable);
        databaseService.executeUpdate(truncateQuery);

        String query = String.format("""
                        SELECT %s
                        FROM %s.%s
                        """,
                DatabaseUtils.generateColumnsDdl(columns, SqlDdl.NAME),
                schema,
                table
        );

        if (DELTA_DATE.equals(synchroType)) {
            log.debug("Filtre date sur la colonne {}", dateColumn);
            String maxQuery = String.format("SELECT MAX(%s) max FROM %s.%s WHERE %s <= '%s'",
                    dateColumn, schema, tmpTable, dateColumn, startDateFormatted
            );
            log.debug(maxQuery);
            LocalDateTime maxDate = databaseService.findFirst(maxQuery, LocalDateTime.class)
                    .orElse(LocalDateTime.of(1970, 1, 1, 0, 0, 0));
            query += String.format("WHERE %s > '%s'", dateColumn, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(maxDate));
        } else if (DELTA_ID.equals(synchroType)) {
            log.debug("Filtre sur l'id");
            String maxQuery = String.format("SELECT MAX(id) max FROM %s.%s",
                    schema, tmpTable
            );
            log.debug(maxQuery);
            Long maxId = databaseService.findFirst(maxQuery, Long.class)
                    .orElse(0L);
            query += String.format("WHERE id > '%s'", maxId);
        }

        log.debug(query);

        // Select sur la PROD
        List<T> dataList = databaseProdService.findAll(query, clazz);

        // Insertion en test
        long nbLignes = databaseService.saveAll(schema, tmpTable, dataList);
        log.info("Table {}, {} lines inserted.", tmpTable, nbLignes);

        // Maj table cible
        long nbNewLines = populateTargetTable(table);
        log.info("Ending sync table {}, {} lines inserted or updated.", table, nbNewLines);
    }

    @SneakyThrows
    private long populateTargetTable(String table) {
        String path = String.format("classpath:sql/%s/%s.sql", batchProperties.key().name().toLowerCase(), table);
        String query = resourceLoader.getResource(path).getContentAsString(StandardCharsets.UTF_8);
        return databaseService.executeUpdate(query);
    }
}
