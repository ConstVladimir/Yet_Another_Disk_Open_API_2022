package com.example.repositories;

import com.example.model.SystemItemFolder;

import java.util.List;
import java.util.Optional;

public interface FolderRepository {
    boolean importFolder (SystemItemFolder folder);
    boolean deleteFolderById (String id);
    Optional<SystemItemFolder> getFolderById (String id);
    Optional<List<SystemItemFolder>> getFoldersByParentId (String parentId);
}
