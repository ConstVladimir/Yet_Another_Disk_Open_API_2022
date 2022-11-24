package com.example.repositories;

import com.example.model.SystemItemFile;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcFileRepository implements FileRepository {
    private JdbcOperations jdbcOperations;
    public JdbcFileRepository(JdbcOperations jdbcOperations){this.jdbcOperations=jdbcOperations;}

    @Override
    public boolean importFile(SystemItemFile file) {
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
                "CALL insert_file (?,?,?,?,?)",
                Types.VARCHAR,Types.VARCHAR, Types.TIMESTAMP_WITH_TIMEZONE, Types.VARCHAR, Types.BIGINT);
        PreparedStatementCreator psc = pscf.newPreparedStatementCreator(
                Arrays.asList(
                        file.getId(),
                        file.getUrl(),
                        file.getDate(),
                        file.getParentId(),
                        file.getSize()
                ));
        jdbcOperations.update(psc);
        return true;
    }

    @Override
    public boolean deleteFileById(String id) {
        return false;
    }

    @Override
    public Optional<SystemItemFile> getFileById(String id) {
        return Optional.empty();
    }

    @Override
    public Optional<List<SystemItemFile>> getFilesByParentId(String parentId) {
        return Optional.empty();
    }
}
