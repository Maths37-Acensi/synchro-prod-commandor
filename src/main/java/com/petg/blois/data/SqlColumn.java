package com.petg.blois.data;

import com.petg.blois.util.PGUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * SqlColumn
 *
 * @author F. LUTZ (97211P)
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SqlColumn {
    protected String schemaName;
    protected String tableName;
    protected Integer position;
    protected String columnName;
    protected String sqlType;
    protected String ddlType;
    protected Boolean nullable;

    public SqlColumn(SqlColumn column) {
        this.schemaName = column.schemaName;
        this.tableName = column.tableName;
        this.position = column.position;
        this.columnName = column.columnName;
        this.sqlType = column.sqlType;
        this.ddlType = column.ddlType;
        this.nullable = column.nullable;
    }

    /**
     * Méthode permettant de compléter les valeurs par défaut avant la génération du ddl.
     */
    public void buildDefaultValue() {
        position = PGUtils.defaultIfEmpty(position, 0);
        ddlType = PGUtils.defaultIfEmpty(ddlType, sqlType);
        nullable = PGUtils.defaultIfEmpty(nullable, true);
    }
}
