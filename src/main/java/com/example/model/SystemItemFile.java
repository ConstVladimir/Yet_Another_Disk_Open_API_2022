package com.example.model;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class SystemItemFile {
    private final String id;
    private final String url;
    private final OffsetDateTime date;
    private final String parentId;
    private final Long size;

    public SystemItemFile (SystemItemImport impItem, OffsetDateTime update){
        this.id = impItem.getId();
        this.url = impItem.getUrl();
        this.date = update;
        this.parentId = impItem.getParentId();
        this.size = impItem.getSize();
    }
}
