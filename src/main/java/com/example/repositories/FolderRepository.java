package com.example.repositories;

import com.example.model.SystemItemFolder;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends CrudRepository<SystemItemFolder, String> {
    @Modifying
    @Query("INSERT INTO SYSTEM_ITEM_FOLDER VALUES (:id, :url, :date, :parentId, :sizeIt, :children);")
    void setItem (String id, String url, long date, String parentId, Long sizeIt, String children);

    @Modifying
    @Query("UPDATE SYSTEM_ITEM_FOLDER SET UPDATE_DATE=:date, SIZE_IT=:sizeIt WHERE ID=:id")
    void updateParentFolder (String id, long date, Long sizeIt);

    @Modifying
    @Query("UPDATE SYSTEM_ITEM_FOLDER SET UPDATE_DATE=:date, SIZE_IT=:sizeIt, CHILDREN=:children WHERE ID=:id")
    void updateParentFolderAndAddItemToChildren (String id, long date, Long sizeIt, String children);

    Optional<SystemItemFolder> getById (String id);
    Optional<List<SystemItemFolder>> findByParentId (String parentId);
}
