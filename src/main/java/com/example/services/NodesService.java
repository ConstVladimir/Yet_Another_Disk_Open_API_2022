package com.example.services;

import com.example.model.SystemItemResponse;
import com.example.repositories.SysItemsRepository;
import org.springframework.stereotype.Service;

@Service
public class NodesService {
    private final SysItemsRepository sysItemsRepository;
    public NodesService(SysItemsRepository sysItemsRepository){this.sysItemsRepository = sysItemsRepository;}
    public SystemItemResponse getSysItem (String id) {
        return sysItemsRepository.getSysItemById(id);
    }
}
