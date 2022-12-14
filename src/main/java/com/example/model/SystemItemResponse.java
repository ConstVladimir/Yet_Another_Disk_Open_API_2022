package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Data
@AllArgsConstructor
public class SystemItemResponse {
    @NotNull
    private final String id;
    private final String url;
    @NotNull
    private final String date;
    private final String parentId;
    @NotNull
    private final SystemItemType type;
    private    Long size;
    private    Set<SystemItemResponse> children;
    public enum SystemItemType {
        FILE, FOLDER
    }

    public static SystemItemResponse cook_system_item (String root, Map<String, SystemItemResponse> table_list_map){
        for (Map.Entry<String, SystemItemResponse> item : table_list_map.entrySet()){
            if (!item.getKey().equals(root)){
                table_list_map.get(item.getValue().getParentId()).getChildren().add(item.getValue());
            }
        }
        table_list_map.get(root).getSize();
        return table_list_map.get(root);
    }
    public Long getSize (){
        if (this.type.equals(SystemItemType.FOLDER)){
            this.size = 0L;
            for (SystemItemResponse child_item : children){
                this.size = Optional.ofNullable(this.size).orElse(0L) + Optional.ofNullable(child_item.getSize()).orElse(0L);
            }
        }
        return this.size;
    }
}
