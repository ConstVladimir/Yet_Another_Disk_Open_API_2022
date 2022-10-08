package com.example.repositories;

import com.example.model.SystemItem;
import com.example.model.SystemItemImport;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SystemItemRepository extends CrudRepository<SystemItem, String> {
    @Modifying
    @Query("INSERT INTO SYSTEM_ITEM VALUES (:id, :url, :date, :parentId, :type, :sizeIt, :children);")
    void setItem (String id, String url, long date, String parentId, SystemItem.SystemItemType type, Long sizeIt, String children);

    @Modifying
    @Query("UPDATE SYSTEM_ITEM SET UPDATE_DATE=:date, SIZE_IT=:sizeIt WHERE ID=:id")
    void updateParentFolder (String id, long date, Long sizeIt);

    @Modifying
    @Query("UPDATE SYSTEM_ITEM SET UPDATE_DATE=:date, SIZE_IT=:sizeIt, CHILDREN=:children WHERE ID=:id")
    void updateParentFolderAndAddItemToChildren (String id, long date, Long sizeIt, String children);

    Optional<SystemItem> getById (String id);
    Optional<SystemItem> getByIdAndType (String id, SystemItem.SystemItemType type);
    Optional<List<SystemItem>> findByParentId (String parentId);
}

/*
    private final String id;
    private final String url;
    private final long date;
    private final String parentId;
    private final SystemItemType type;
    private final Integer size;
    private final List<String> children;*/