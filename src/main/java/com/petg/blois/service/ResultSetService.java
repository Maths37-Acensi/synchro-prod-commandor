package com.petg.blois.service;

import com.petg.blois.exception.ServiceException;
import com.petg.blois.util.PGUtils;
import com.petg.blois.util.ReflectionPropertyDescriptor;
import com.petg.blois.util.ReflectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ResultSetService
 *
 * @author F. LUTZ (97211P)
 */
@Service
@RequiredArgsConstructor
public class ResultSetService {
    /**
     * Get a list of clazz objets that contains results of query
     * T must be default Java class (int, Long, String, Date...) or object that contains attribute of default Java class
     *
     * @param rs           result set to convert
     * @param clazz        object that represents query results
     * @param onlyFirstRow if true, return only the first row, otherwise return all rows
     * @param <T>          Type of clazz
     * @return list of T
     */
    public <T> List<T> mapToObjects(ResultSet rs, Class<T> clazz, boolean onlyFirstRow) {
        List<T> values = new ArrayList<>();

        try {
            List<ReflectionPropertyDescriptor> descriptors = ReflectionUtils.findAllPropertyDescriptors(clazz);

            while (rs.next()) {
                mapToObject(rs, clazz, descriptors)
                        .ifPresent(values::add);
                // We stopped here if we want only the first row
                if (onlyFirstRow) {
                    break;
                }
            }
        } catch (SQLException e) {
            throw new ServiceException(e);
        }

        return values;
    }

    /**
     * Convert a row of ResultSet to a class
     *
     * @param rs          ResultSet
     * @param clazz       Target class
     * @param descriptors List of class descriptors
     * @param <T>         Type of clazz
     * @return T
     * @throws SQLException if a database access errorFile occurs or this method is called on a closed result set
     */
    public <T> Optional<T> mapToObject(ResultSet rs, Class<T> clazz, List<ReflectionPropertyDescriptor> descriptors) throws SQLException {
        if (rs.getMetaData().getColumnCount() == 0) {
            // No data
            return Optional.empty();
        } else if (descriptors.isEmpty()) {
            // If the target class is a primitive class or wrapped primitive class (without getter and setter)
            Object fieldValue = rs.getObject(1);
            return Optional.ofNullable(ReflectionUtils.cast(fieldValue, clazz));
        } else {
            // Complex object, call constructor and call each setter
            T bean = ReflectionUtils.findAndCallConstructor(clazz)
                    .orElseThrow(() -> new ServiceException(String.format("Missing no args constructor of class %s", clazz.getName())));
            for (int position = 1; position <= rs.getMetaData().getColumnCount(); position++) {
                String colName = PGUtils.snakeToCamelCase(rs.getMetaData().getColumnName(position).toLowerCase(), false);
                Object fieldValue = rs.getObject(position);
                if (fieldValue == null) {
                    continue;
                }
                descriptors.stream()
                        .filter(descriptor -> descriptor.getField().getName().equalsIgnoreCase(colName))
                        .findFirst()
                        .ifPresent(descriptor ->
                                ReflectionUtils.writeField(descriptor, bean, ReflectionUtils.cast(fieldValue, descriptor.getField().getType()))
                        );
            }
            return Optional.of(bean);
        }
    }
}

