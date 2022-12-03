package com.example.repositories;

import com.example.model.SystemItem;
import com.example.model.SystemItemFolder;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.*;

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
    public List<SystemItem> getFolderById(String id) {
        List<SystemItem> puff = jdbcOperations.query(String.format("SELECT * FROM get_item ('%s');", id), this::mapRowToSystemItem);


        return puff;
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
                    row.getString("size_it"),
                    new HashSet<SystemItem>()
            );
    }
}
