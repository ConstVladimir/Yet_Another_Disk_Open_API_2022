package com.example.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table
@Data
public class SystemItemFolder {
    @Id
    private final String id;
    private final String url;
    @Column("UPDATE_DATE")
    private final long date;
    private final String parentId;
    @Column ("SIZE_IT")
    private final Long size;
    private final String children;
}
