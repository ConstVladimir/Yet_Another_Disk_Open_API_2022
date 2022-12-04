package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.OffsetDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
public class SystemItem {
    @NotNull
    private final String id;
    private final String url;
    @NotNull
    private final String date;
    private final String parentId;
    @NotNull
    private final SystemItemType type;
    private    Long size;
    private    Set<SystemItem> children;
    public enum SystemItemType {
        FILE, FOLDER
    }
}
