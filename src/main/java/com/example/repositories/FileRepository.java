package com.example.repositories;

import com.example.model.SystemItemFile;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends CrudRepository<SystemItemFile, String> {
    @Modifying
    @Query("INSERT INTO SYSTEM_ITEM_FILE VALUES (:id, :url, :date, :parentId, :sizeIt);")
    void setItem (String id, String url, long date, String parentId, Long sizeIt);

    Optional<SystemItemFile> getById (String id);
    Optional<List<SystemItemFile>> findByParentId (String parentId);
}
