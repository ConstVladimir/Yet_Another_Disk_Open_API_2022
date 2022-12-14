import com.example.model.SystemItemResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class testSystemItemResponse {
    SystemItemResponse root_id = new SystemItemResponse("root",//"069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
            null,
            "2022-02-03T15:00:00Z",
            null,
            SystemItemResponse.SystemItemType.FOLDER,
            null,
            new HashSet<>());
    SystemItemResponse folder1_id = new SystemItemResponse("folder1",//"1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
            null,
            "2022-02-03T15:00:00Z",
            "root",//"069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
            SystemItemResponse.SystemItemType.FOLDER,
            null,
            new HashSet<>());
    SystemItemResponse folder2_id = new SystemItemResponse("folder2",//"d515e43f-f3f6-4471-bb77-6b455017a2d2",
            null,
            "2022-02-02T12:00:00Z",
            "root",//"069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
            SystemItemResponse.SystemItemType.FOLDER,
            null,
            new HashSet<>());
    SystemItemResponse file1_id = new SystemItemResponse("file1",//"863e1a7a-1304-42ae-943b-179184c077e3",
            "/file/url1",
            "2022-02-02T12:00:00Z",
            "folder2",//"d515e43f-f3f6-4471-bb77-6b455017a2d2",
            SystemItemResponse.SystemItemType.FILE,
            128L,
            new HashSet<>());
    SystemItemResponse file2_id = new SystemItemResponse("file2",//"b1d8fd7d-2ae3-47d5-b2f9-0f094af800d4",
            "/file/url1",
            "2022-02-02T12:00:00Z",
            "folder2",//"d515e43f-f3f6-4471-bb77-6b455017a2d2",
            SystemItemResponse.SystemItemType.FILE,
            256L,
            new HashSet<>());
    /*

    */
    @Test
    @DisplayName("existing_file")
    void existing_file(){
        Map<String, SystemItemResponse> dataBaseResponse = new HashMap<>();
        dataBaseResponse.put(file1_id.getId(), file1_id);
        SystemItemResponse result = SystemItemResponse.cook_system_item(file1_id.getId(), dataBaseResponse);
        assertEquals(128L, result.getSize());
        assertTrue(result.getChildren().isEmpty());
    }
    @Test
    @DisplayName("existing_empty_folder")
    void  existing_empty_folder(){
        Map<String, SystemItemResponse> dataBaseResponse = new HashMap<>();
        dataBaseResponse.put(folder1_id.getId(), folder1_id);
        SystemItemResponse result = SystemItemResponse.cook_system_item(folder1_id.getId(), dataBaseResponse);
        assertEquals(0,result.getSize());
        assertTrue(result.getChildren().isEmpty());
    }
    @Test
    @DisplayName("existing_not_empty_folder")
    void  existing_not_empty_folder(){
        Map<String, SystemItemResponse> dataBaseResponse = new HashMap<>();
        dataBaseResponse.put(root_id.getId(), root_id);
        dataBaseResponse.put(folder1_id.getId(),folder1_id);
        dataBaseResponse.put(folder2_id.getId(),folder2_id);
        dataBaseResponse.put(file1_id.getId(),file1_id);
        dataBaseResponse.put(file2_id.getId(), file2_id);
        SystemItemResponse result = SystemItemResponse.cook_system_item(root_id.getId(), dataBaseResponse);
        assertEquals(384L,result.getSize());
        assertTrue(result.getChildren().size() == 2);
    }
}
