package com.example.repositories;

import com.example.model.SystemItemResponse;
import com.example.model.SystemItemImport;

import java.time.OffsetDateTime;

public interface SysItemsRepository {
    boolean importSysItem (SystemItemImport item, String date);
    SystemItemResponse getSysItemById (String id);
    boolean deleteSysItemById (String id, OffsetDateTime date);
}
