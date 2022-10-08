package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Table
@Data
@AllArgsConstructor
public class SystemItem {
    @Id
    private final String id;
    private final String url;
    @Column ("UPDATE_DATE")
    private final long date;
    private final String parentId;
    private final SystemItemType type;
    @Column ("SIZE_IT")
    private final Long size;
    private final String children;

    public enum SystemItemType {
        FILE, FOLDER
    }
}

