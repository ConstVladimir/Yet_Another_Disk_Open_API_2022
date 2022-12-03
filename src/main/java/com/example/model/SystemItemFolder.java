package com.example.model;

import lombok.Data;


import java.time.OffsetDateTime;
import java.util.Set;

@Data
public class SystemItemFolder {
    private final String id;
    private final String url;
    private final OffsetDateTime date;
    private final String parentId;
    private final Long size;
    private final Set<String> children;

    public  SystemItemFolder (SystemItemImport impItem, OffsetDateTime update){
        this.id = impItem.getId();
        this.url = null;
        this.date = update;
        this.parentId = impItem.getParentId();
        this.size = null;
        this.children = null;
    }
}
