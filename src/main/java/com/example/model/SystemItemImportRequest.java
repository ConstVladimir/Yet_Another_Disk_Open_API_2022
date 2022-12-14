package com.example.model;

import com.example.model.SystemItemImport;
import lombok.Data;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class SystemItemImportRequest {
    @NotNull
    private final List<@Valid SystemItemImport> items;
    @NotNull
    private final String updateDate;
}
