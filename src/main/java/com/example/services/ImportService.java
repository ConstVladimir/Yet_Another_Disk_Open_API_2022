package com.example.services;

import com.example.dto.SystemItemImportRequest;
import com.example.exceptions.SystemItemImportException;
import com.example.model.SystemItem;
import com.example.model.SystemItemFile;
import com.example.model.SystemItemImport;
import com.example.repositories.FileRepository;
import com.example.repositories.FolderRepository;
import com.example.repositories.SystemItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

@Service
public class ImportService {
    //private final SystemItemRepository systemItemRepository;
    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;
    public ImportService(FolderRepository folderRepository, FileRepository fileRepository){
        this.folderRepository = folderRepository;
        this.fileRepository = fileRepository;
    }

    public boolean insertItems (SystemItemImportRequest systemItemImportRequest){

        long longDate = ZonedDateTime.parse(systemItemImportRequest.getUpdateDate(),ISO_DATE_TIME)
                .toInstant()
                .toEpochMilli();

        for (SystemItemImport i : systemItemImportRequest.getItems()) {
            if (i.getType().equals(SystemItemImport.SystemItemType.FILE)){
                Optional<SystemItemFile> past_item = fileRepository.getById(i.getId());
                /*
                Если существует файл в таблице
                    получить дерево родительских папок
                        если новая р.папка и прежня совпадают
                            переписать размер размер в дереве -прошлый+новый размер
                        если новая р.папка другая (перенос файла)
                            перепись размера у старого древа размеров -прошлый размер
                            запись нового древа размер + новый размер
                            добавление в список н.родителя, удаление у старого
                Если нет записи о файле в таблицу
                    построить древо родителей
                        записать всем +размер файла
                        добавить в список родителя файл
                Обновить запись о файле
                 */
            }
            else if (i.getType().equals(SystemItemImport.SystemItemType.FOLDER)){
                /*
                Если существует папка в таблице
                    сравнить старого нового родителя
                        если отличаются
                            у древа старого -размер папки
                            убрать ее из списка родителя
                            у новго древа + размер папки
                            добавить имя в список детей нового родителя
                        если родители совпадают
                            сравнить url и при отличии обновить на новое значение
                Если папки нет
                    Занести данные о папке в таблицу
                 */
            }
            else throw new SystemItemImportException(String.format("Unknown insert item type %s", i.getType()));


        }
        return true;
    }

    private void updatingSystemItemInF (SystemItemImport i, long longDate, SystemItem past_item){
        SystemItem currentItem;
        if (i.getType() == SystemItemImport.SystemItemType.FILE) {
            currentItem = new SystemItem(i.getId(),i.getUrl(),longDate,i.getParentId(),i.getType(),i.getSize(),null);

        } else if (i.getType() == SystemItemImport.SystemItemType.FOLDER) {
                currentItem = new SystemItem(i.getId(), i.getUrl(), longDate, i.getParentId(), i.getType(), past_item.getSize(), past_item.getChildren());
        }
        HashMap<String,Long> parentTreeCurrent = treeOfParentWithSize (i.getParentId());
    }

    @Transactional
    private void creatingNewSystemItem(SystemItemImport i, long longDate) {
        SystemItem currentItem = new SystemItem(i.getId(), i.getUrl(), longDate,
                                            i.getParentId(), i.getType(), i.getSize(), null);
        HashMap<String,Long> parentTreeCurrent = treeOfParentWithSize(currentItem.getParentId());

        // ошибка в функции save  вынуждает делать свой метод
        systemItemRepository.setItem(currentItem.getId(),
                currentItem.getUrl(),
                currentItem.getDate(),
                currentItem.getParentId(),
                currentItem.getType(),
                currentItem.getSize(),
                currentItem.getChildren());

        if (!parentTreeCurrent.isEmpty()){
            // updating Mother FOLDER
            SystemItem exist_parent = getParent(currentItem.getParentId());
            systemItemRepository.updateParentFolderAndAddItemToChildren(
                    currentItem.getParentId(),
                    longDate,
                    Optional.ofNullable(parentTreeCurrent.remove(currentItem.getParentId())).orElse(0L)+Optional.ofNullable(currentItem.getSize()).orElse(0L), //скрывается null при неудалении ненайденного элемента
                    addFileToChildrenString(exist_parent.getChildren(), currentItem.getId()));
            // updating next FOLDERS on tree
            for (Map.Entry<String, Long> pair : parentTreeCurrent.entrySet()) {
                systemItemRepository.updateParentFolder(
                        pair.getKey(),
                        longDate,
                        Optional.ofNullable(pair.getValue()).orElse(0L) + Optional.ofNullable(currentItem.getSize()).orElse(0L)
                );
            }
        }
    }

    private SystemItem getParent (String parentId){
        SystemItem parent = systemItemRepository.getByIdAndType(parentId, SystemItem.SystemItemType.FOLDER)
                .orElseThrow(()->new SystemItemImportException(
                        String.format("ParentId %s %s not found",
                                parentId,
                                SystemItem.SystemItemType.FOLDER)));
        return parent;
    }

    private HashMap<String, Long> treeOfParentWithSize (String parentId){
        HashMap<String, Long> parentTree = new HashMap<String, Long>();
        while (parentId != null){
            String finalParentId = parentId;
            SystemItem parent = systemItemRepository.getByIdAndType(parentId, SystemItem.SystemItemType.FOLDER)
                    .orElseThrow(()->new SystemItemImportException(
                            String.format("ParentId %s %s not found when building a tree",
                                    finalParentId,
                                    SystemItem.SystemItemType.FOLDER)));
            parentTree.put(parent.getId(),parent.getSize());
            parentId = parent.getParentId();
        }
        return parentTree;
    }

    private boolean isFileTheChild (String children, String itemId){
        return Arrays.stream(children.split(",")).anyMatch(t->itemId.equals(t));
    }

    private String addFileToChildrenString (String children, String itemId){
        if (isFileTheChild(children,itemId)){
            return  children;
        }
        else return children.concat(","+itemId);
    }

    private String removeFileFromChildrenString (String children, String itemId){
        Set <String> childrenSet = Arrays.stream(children.split(",")).collect(Collectors.toSet());
        childrenSet.remove(itemId);
        return String.join(",",childrenSet);
    }
}
