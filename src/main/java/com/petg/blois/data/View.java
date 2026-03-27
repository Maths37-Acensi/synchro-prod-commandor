package com.petg.blois.data;

public record View(
        String viewName,
        String viewDefinition,
        String oldTableName,
        String newTableName
) {
}
