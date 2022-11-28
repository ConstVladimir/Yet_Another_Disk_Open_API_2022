package com.example.services;

import com.example.repositories.FileRepository;
import com.example.repositories.FolderRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class NodesService {
    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;
    public NodesService(FolderRepository folderRepository, FileRepository fileRepository){
        this.folderRepository = folderRepository;
        this.fileRepository = fileRepository;
    }
    /*Optional<SystemItem> nodeItem (String id){
        return true;
    }*/
}
