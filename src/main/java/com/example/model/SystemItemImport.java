package com.example.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class SystemItemImport {
    @NotNull
    private final String id;
    private final String url;
    private final String parentId;
    @NotNull
    private final SystemItemType type;
    private final Long size; // добавить контроль size у папок при получении request

    public enum SystemItemType {
        FILE, FOLDER
    }
}