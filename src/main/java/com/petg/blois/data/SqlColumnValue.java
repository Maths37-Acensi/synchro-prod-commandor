package com.petg.blois.data;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * SqlColumnValue
 *
 * @author F. LUTZ (97211P)
 */
@Data
@With
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SqlColumnValue extends SqlColumn {
    private String value;
    private String sql;

    public SqlColumnValue(SqlColumnValue sqlColumnValue) {
        super(sqlColumnValue);
        this.value = sqlColumnValue.value;
        this.sql = sqlColumnValue.sql;
    }
}

