package com.example.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

@Table
@Data
public class SystemItemFile {
    @Id
    private final String id;
    private final String url;
    @Column("update_date")
    private final Timestamp date;
    private final String parentId;
    @Column ("size_it")
    private final Long size;
}
