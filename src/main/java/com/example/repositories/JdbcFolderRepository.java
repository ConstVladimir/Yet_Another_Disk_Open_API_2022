package com.example.repositories;

import com.example.model.SystemItem;
import com.example.model.SystemItemFolder;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

@Repository
public class JdbcFolderRepository implements FolderRepository {
    private JdbcOperations jdbcOperations;
    public JdbcFolderRepository(JdbcOperations jdbcOperations){this.jdbcOperations=jdbcOperations;}

    @Override
    public boolean importFolder(SystemItemFolder folder) {
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
                "CALL insert_folder (?,?,?)",
                Types.VARCHAR, Types.TIMESTAMP_WITH_TIMEZONE, Types.VARCHAR);
        PreparedStatementCreator psc = pscf.newPreparedStatementCreator(
                Arrays.asList(
                        folder.getId(),
                        folder.getDate(),
                        folder.getParentId()
                ));
        jdbcOperations.update(psc);
        return true;
    }

    @Override
    public boolean deleteFolderById(String id, OffsetDateTime date) {
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
                "CALL delete_item (?,?);",
                Types.VARCHAR, Types.TIMESTAMP_WITH_TIMEZONE);
        PreparedStatementCreator psc = pscf.newPreparedStatementCreator(
                Arrays.asList(id, date));
        jdbcOperations.update(psc);
        return true;
    }

    @Override
    public SystemItem getFolderById(String id) {
        //SqlRowSet puff = jdbcOperations.queryForRowSet(String.format("SELECT * FROM get_item ('%s');", id));
        Map<String,SystemItem> rows = jdbcOperations.queryForStream(String.format("SELECT * FROM get_item ('%s');", id),this::mapRowToSystemItem).collect(Collectors.toMap(t-> t.getId(), t->t));
        //List<SystemItem> puff = jdbcOperations.query(String.format("SELECT * FROM get_item ('%s');", id), this::mapRowToSystemItem);
        //Map<String,SystemItem> folders = puff.stream().filter(item->item.getType().equals(SystemItem.SystemItemType.FOLDER)).collect(Collectors.toMap(it->it.getId(),it->it));
        //Map<String,SystemItem> files = puff.stream().filter(item->item.getType().equals(SystemItem.SystemItemType.FILE)).collect(Collectors.toMap(it->it.getId(),it->it));
        for (Map.Entry<String, SystemItem> item : rows.entrySet()){
            if (!item.getKey().equals(id)){
                rows.get(item.getValue().getParentId()).getChildren().add(item.getValue());
            }
        }
        rows.get(id).getSize();
        return rows.get(id);
    }

    @Override
    public Optional<List<SystemItemFolder>> getFoldersByParentId(String parentId) {
        return Optional.empty();
    }

    private SystemItem mapRowToSystemItem (ResultSet row, int rowNum) throws SQLException {
            return new SystemItem(
                    row.getString("id"),
                    row.getString("url"),
                    //row.getTimestamp("update_date"),
                    row.getString("update_date").replace(" ", "T"),
                    row.getString ("parent_id"),
                    SystemItem.SystemItemType.valueOf(row.getString ("item_t")),
                    row.getLong("size_it"),
                    new HashSet<SystemItem>()
            );
    }
}
