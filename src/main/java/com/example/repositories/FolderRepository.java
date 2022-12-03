package com.example.repositories;

import com.example.model.SystemItemFolder;
import com.example.model.SystemItem;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface FolderRepository {
    boolean importFolder (SystemItemFolder folder);
    boolean deleteFolderById (String id, OffsetDateTime date);
    List<SystemItem> getFolderById (String id);
    Optional<List<SystemItemFolder>> getFoldersByParentId (String parentId);
}
