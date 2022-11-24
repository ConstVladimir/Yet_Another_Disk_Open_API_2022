package com.example.repositories;

import com.example.model.SystemItemFile;
import java.util.List;
import java.util.Optional;

public interface FileRepository {
    boolean importFile (SystemItemFile file);
    boolean deleteFileById (String id);
    Optional<SystemItemFile> getFileById (String id);
    Optional<List<SystemItemFile>> getFilesByParentId (String parentId);
}
