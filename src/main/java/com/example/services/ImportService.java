package com.example.services;

import com.example.dto.SystemItemImportRequest;
import com.example.exceptions.SystemItemImportException;
import com.example.model.SystemItemFile;
import com.example.model.SystemItemFolder;
import com.example.model.SystemItemImport;
import com.example.repositories.FileRepository;
import com.example.repositories.FolderRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

@Service
public class ImportService {
    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;
    public ImportService(FolderRepository folderRepository, FileRepository fileRepository){
        this.folderRepository = folderRepository;
        this.fileRepository = fileRepository;
    }

    public boolean insertItems (SystemItemImportRequest systemItemImportRequest){

        String dateStr = systemItemImportRequest.getUpdateDate();

        OffsetDateTime dateSQL = ZonedDateTime.parse(dateStr,ISO_DATE_TIME).toOffsetDateTime();

        for (SystemItemImport i : systemItemImportRequest.getItems()) {
            if (i.getType().equals(SystemItemImport.SystemItemType.FILE)){
                SystemItemFile ImpFile = new SystemItemFile(i, dateSQL);
                fileRepository.importFile(ImpFile);
            }
            else if (i.getType().equals(SystemItemImport.SystemItemType.FOLDER)){
                SystemItemFolder ImpFolder = new SystemItemFolder(i, dateSQL);
                folderRepository.importFolder(ImpFolder);
            }
            else throw new SystemItemImportException(String.format("Unknown insert item type %s", i.getType()));
        }
        return true;
    }
}
