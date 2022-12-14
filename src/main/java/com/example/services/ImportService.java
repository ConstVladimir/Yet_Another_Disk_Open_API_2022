package com.example.services;

import com.example.model.SystemItemImportRequest;
import com.example.model.SystemItemImport;
import com.example.repositories.SysItemsRepository;
import org.springframework.stereotype.Service;

@Service
public class ImportService {
    private final SysItemsRepository sysItemsRepository;
    public ImportService(SysItemsRepository sysItemsRepository){
        this.sysItemsRepository = sysItemsRepository;
    }

    public boolean insertItems (SystemItemImportRequest systemItemImportRequest){

        String date = systemItemImportRequest.getUpdateDate();

        for (SystemItemImport i : systemItemImportRequest.getItems()) {
            sysItemsRepository.importSysItem(i,date);
        }
        return true;
    }
}
