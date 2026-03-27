package com.petg.blois.util;


import com.petg.blois.data.DdlOptions;
import com.petg.blois.data.SqlColumn;
import com.petg.blois.data.SqlColumnValue;
import com.petg.blois.data.SqlDdl;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;


/**
 * SqlUtils
 * Classe utilitaire pour les interactions avec les bases de données.
 *
 * @author F. LUTZ (97211P)
 */
@Slf4j
@UtilityClass
public class DatabaseUtils {
    /**
     * Permet de générer le ddl associé aux colonnes suivant le SqlDdl.
     *
     * @param columns       Liste des colonnes de la table.
     * @param sqlDdl        Type de script désiré (nom, type ou valeur).
     * @param ddlOptionsOpt Option pour la génération du script.
     * @return ddl associé aux colonnes.
     */
    public String generateColumnsDdl(List<SqlColumn> columns, SqlDdl sqlDdl, DdlOptions... ddlOptionsOpt) {
        return columns.stream()
                .peek(SqlColumn::buildDefaultValue)
                .sorted(Comparator.comparingInt(SqlColumn::getPosition))
                .map(column -> generateColumnDdl(column, sqlDdl, ddlOptionsOpt))
                .reduce((c1, c2) -> String.format("%s, %s", c1, c2))
                .orElse("");
    }

    public String generateColumnName(SqlColumn column, DdlOptions... ddlOptions) {
        return column.getColumnName();
    }

    public String generateColumnDdl(SqlColumn column, SqlDdl sqlDdl, DdlOptions... ddlOptionsOpt) {
        DdlOptions options = PGUtils.findFirstParameter(ddlOptionsOpt)
                .orElse(DdlOptions.builder().build());
        String columnName = generateColumnName(column, options);
        String columnValue = columnName;

        if (SqlDdl.TYPE.equals(sqlDdl)) {
            return String.format("%s %s %s", columnName, column.getDdlType(), column.getNullable() ? "" : "NOT NULL");
        } else if (SqlDdl.VALUE.equals(sqlDdl)) {
            if (column instanceof SqlColumnValue value) {
                if (StringUtils.isNoneEmpty(value.getSql())) {
                    columnValue = value.getSql();
                } else {
                    columnValue = StringUtils.isEmpty(value.getValue()) ? "NULL" : String.format("'%s'", value.getValue());
                }
            }
            return String.format("%s AS %s", columnValue, columnName);
        } else {
            return columnName;
        }

    }

    /**
     * Permet de convertir un objet Java en sql (formatage).
     *
     * @param value Valeur à convertir.
     * @return La valeur associée en sql formaté.
     */
    public String javaToSql(Object value) {
        return switch (value) {
            case null -> "NULL";
            case Boolean bool -> bool ? "1" : "0";
            case LocalDate ldValue -> String.format("'%s'", DateTimeFormatter.ISO_LOCAL_DATE.format(ldValue));
            case LocalTime ltValue ->
                    String.format("'%s'", DateTimeFormatter.ISO_LOCAL_TIME.format(ltValue.withNano(0)));
            case LocalDateTime ldtValue ->
                    String.format("'%s'", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(ldtValue.withNano(0)));
            case ZonedDateTime zdtValue ->
                    String.format("'%s'", DateTimeFormatter.ISO_ZONED_DATE_TIME.format(zdtValue.withNano(0)));
            case String sValue -> String.format("'%s'", sValue.replace("'", "''"));
            case Enum<?> eValue -> String.format("'%s'", eValue.name());
            case List<?> lValues -> String.format("'%s'", lValues.stream()
                    .map(DatabaseUtils::javaToSql)
                    .reduce((s1, s2) -> String.format("%s, %s", s1, s2))
                    .orElse(""));
            default -> value.toString();
        };
    }
}
