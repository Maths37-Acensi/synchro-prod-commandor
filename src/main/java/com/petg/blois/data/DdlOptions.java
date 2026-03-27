package com.petg.blois.data;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * Builder used to convert a list of GreenplumColumn object to SQL (instruction for create, select or insert).
 *
 * @author F. LUTZ (97211P)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DdlOptions {
    @Builder.Default
    private boolean quote = true;
    @Builder.Default
    private char decimalSeparator = '\u0000';
    @Builder.Default
    private String nullValue = StringUtils.EMPTY;
    @Builder.Default
    private String trueValue = StringUtils.EMPTY;
    @Builder.Default
    private String falseValue = StringUtils.EMPTY;
    @Builder.Default
    private String datePattern = StringUtils.EMPTY;
    @Builder.Default
    private String timePattern = StringUtils.EMPTY;
    @Builder.Default
    private String dateTimePattern = StringUtils.EMPTY;
}
