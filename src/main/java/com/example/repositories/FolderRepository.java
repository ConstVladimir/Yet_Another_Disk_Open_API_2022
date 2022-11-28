package com.example.repositories;

import com.example.model.SystemItemFolder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface FolderRepository {
    boolean importFolder (SystemItemFolder folder);
    boolean deleteFolderById (String id, OffsetDateTime date);
    Optional<SystemItemFolder> getFolderById (String id);
    Optional<List<SystemItemFolder>> getFoldersByParentId (String parentId);
}
