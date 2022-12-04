package com.example.services;

import com.example.model.SystemItem;
import com.example.model.SystemItemFolder;
import com.example.repositories.FileRepository;
import com.example.repositories.FolderRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class NodesService {
    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;
    public NodesService(FolderRepository folderRepository, FileRepository fileRepository){
        this.folderRepository = folderRepository;
        this.fileRepository = fileRepository;
    }

    public SystemItem getFolder (String id) {
        return folderRepository.getFolderById(id);
    }
}
