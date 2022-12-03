package com.example.services;

import com.example.repositories.FileRepository;
import com.example.repositories.FolderRepository;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

@Service
public class DeleteService {
    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;
    public DeleteService(FolderRepository folderRepository, FileRepository fileRepository){
        this.folderRepository = folderRepository;
        this.fileRepository = fileRepository;
    }

    public boolean deleteItem ( String id, OffsetDateTime date){
        fileRepository.deleteFileById(id, date);
        //folderRepository.deleteFolderById(id, date);
        return true;
    }
}
