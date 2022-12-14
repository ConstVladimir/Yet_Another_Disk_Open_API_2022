package com.example.repositories;

import com.example.exceptions.SystemItemDeleteException;
import com.example.exceptions.SystemItemImportException;
import com.example.exceptions.SystemItemNodesException;
import com.example.model.SystemItemResponse;
import com.example.model.SystemItemImport;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
@Repository
public class JdbcTmplSysItemsRepository implements  SysItemsRepository{
    private JdbcOperations jdbcOperations;
    public JdbcTmplSysItemsRepository(JdbcOperations jdbcOperations){this.jdbcOperations=jdbcOperations;}

    @Override
    public boolean importSysItem(SystemItemImport item, String date) {
        OffsetDateTime dateSQL = ZonedDateTime.parse(date,ISO_DATE_TIME).toOffsetDateTime();
        PreparedStatementCreatorFactory pscf;
        PreparedStatementCreator psc;
        if (item.getType().equals(SystemItemImport.SystemItemType.FOLDER)){
            pscf = new PreparedStatementCreatorFactory(
                    "CALL insert_folder (?,?,?)",
                    Types.VARCHAR, Types.TIMESTAMP_WITH_TIMEZONE, Types.VARCHAR);
            psc = pscf.newPreparedStatementCreator(
                    Arrays.asList(
                            item.getId(),
                            dateSQL,
                            item.getParentId()
                    ));
        } else if (item.getType().equals(SystemItemImport.SystemItemType.FILE)) {
            pscf = new PreparedStatementCreatorFactory(
                    "CALL insert_file (?,?,?,?,?);",
                    Types.VARCHAR,Types.VARCHAR, Types.TIMESTAMP_WITH_TIMEZONE, Types.VARCHAR, Types.BIGINT);
            psc = pscf.newPreparedStatementCreator(
                    Arrays.asList(
                            item.getId(),
                            item.getUrl(),
                            dateSQL,
                            item.getParentId(),
                            item.getSize()
                    ));
        }
        else throw new SystemItemImportException(String.format("Unknown insert item type %s", item.getType()));
        jdbcOperations.update(psc);
        return true;
    }

    @Override
    public SystemItemResponse getSysItemById(String id) {
        Map<String, SystemItemResponse> rows = jdbcOperations.queryForStream(String.format("SELECT * FROM get_item ('%s');", id),this::mapRowToSystemItem).collect(Collectors.toMap(t-> t.getId(), t->t));
        if (rows.isEmpty()) throw new SystemItemNodesException(String.format("%s not found", id));
        return SystemItemResponse.cook_system_item(id, rows);
    }

    @Override
    public boolean deleteSysItemById(String id, OffsetDateTime date) {
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
                "CALL delete_item (?,?);",
                Types.VARCHAR, Types.TIMESTAMP_WITH_TIMEZONE);
        PreparedStatementCreator psc = pscf.newPreparedStatementCreator(
                Arrays.asList(id, date));
        try{
            jdbcOperations.update(psc);
        }
        catch (Exception e){
            throw new SystemItemDeleteException(e.getMessage());
        }
        return true;
    }

    private SystemItemResponse mapRowToSystemItem (ResultSet row, int rowNum) throws SQLException {
        return new SystemItemResponse(
                row.getString("id"),
                row.getString("url"),
                row.getString("update_date").replace(" ", "T"),
                row.getString ("parent_id"),
                SystemItemResponse.SystemItemType.valueOf(row.getString ("item_t")),
                row.getLong("size_it"),
                new HashSet<SystemItemResponse>()
        );
    }
}
