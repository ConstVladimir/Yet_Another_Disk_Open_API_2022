package com.example.services;

import com.example.repositories.SysItemsRepository;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;

@Service
public class DeleteService {
    private final SysItemsRepository sysItemsRepository;
    public DeleteService(SysItemsRepository sysItemsRepository){
        this.sysItemsRepository = sysItemsRepository;
    }

    public boolean deleteItem ( String id, OffsetDateTime date){
        sysItemsRepository.deleteSysItemById(id, date);
        return true;
    }
}
