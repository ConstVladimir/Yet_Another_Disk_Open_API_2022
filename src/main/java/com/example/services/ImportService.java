package com.example.services;

import com.example.dto.SystemItemImportRequest;
import com.example.exceptions.SystemItemImportException;
import com.example.model.SystemItem;
import com.example.model.SystemItemFile;
import com.example.model.SystemItemFolder;
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
                if (past_item.isPresent()){ //Если существует файл в таблице
                    //SystemItemFolder past_parent = getParent(past_item.get().getParentId());
                    if (past_item.get().getParentId().equals(i.getParentId())){ //если новая р.папка и прежня совпадают
                        HashMap<String,Long> tree = treeOfParentWithSize(i.getParentId());

                        for (Map.Entry<String, Long> pair : tree.entrySet()) {
                            folderRepository.updateParentFolder(
                                    pair.getKey(),
                                    longDate,
                                    Optional.ofNullable(pair.getValue()).orElse(0L)
                                            - past_item.get().getSize()
                                            + Optional.ofNullable(i.getSize()).orElse(0L)
                            );
                        }
                    }
                    else { //если новая р.папка другая
                        HashMap<String,Long> past_tree = treeOfParentWithSize(past_item.get().getParentId());
                        HashMap<String,Long> present_tree = treeOfParentWithSize(i.getParentId());

                        if (!past_tree.isEmpty()){ //перепись размера у старого древа размеров - прошлый размер
                            // updating Mother FOLDER
                            SystemItemFolder past_parent = getParent(past_item.get().getParentId());
                            folderRepository.updateParentFolderAndAddItemToChildren(
                                    past_item.get().getParentId(),
                                    longDate,
                                    Optional.ofNullable(past_tree.remove(past_item.get().getParentId())).orElse(0L)-Optional.ofNullable(past_item.get().getSize()).orElse(0L), //скрывается null при неудалении ненайденного элемента
                                    removeFileFromChildrenString(past_parent.getChildren(), past_item.get().getId()));
                            // updating next FOLDERS on tree
                            for (Map.Entry<String, Long> pair : past_tree.entrySet()) {
                                folderRepository.updateParentFolder(
                                        pair.getKey(),
                                        longDate,
                                        Optional.ofNullable(pair.getValue()).orElse(0L) - Optional.ofNullable(past_item.get().getSize()).orElse(0L)
                                );
                            }
                        }

                        if (!present_tree.isEmpty()){ //запись нового древа размер + новый размер
                            // updating Mother FOLDER
                            SystemItemFolder present_parent = getParent(i.getParentId());
                            folderRepository.updateParentFolderAndAddItemToChildren(
                                    i.getParentId(),
                                    longDate,
                                    Optional.ofNullable(present_tree.remove(i.getParentId())).orElse(0L)+Optional.ofNullable(i.getSize()).orElse(0L), //скрывается null при неудалении ненайденного элемента
                                    addFileToChildrenString(present_parent.getChildren(), i.getId()));
                            // updating next FOLDERS on tree
                            for (Map.Entry<String, Long> pair : present_tree.entrySet()) {
                                folderRepository.updateParentFolder(
                                        pair.getKey(),
                                        longDate,
                                        Optional.ofNullable(pair.getValue()).orElse(0L) + Optional.ofNullable(i.getSize()).orElse(0L)
                                );
                            }
                        }

                    }
                    fileRepository.save(new SystemItemFile(i.getId(),i.getUrl(),longDate,i.getParentId(),i.getSize()));
                }
                else { //Если нет записи о файле в таблицу
                    HashMap<String,Long> present_tree = treeOfParentWithSize(i.getParentId());
                    if (!present_tree.isEmpty()){ //запись нового древа размер + новый размер
                        // updating Mother FOLDER
                        SystemItemFolder present_parent = getParent(i.getParentId());
                        folderRepository.updateParentFolderAndAddItemToChildren(
                                i.getParentId(),
                                longDate,
                                Optional.ofNullable(present_tree.remove(i.getParentId())).orElse(0L)+Optional.ofNullable(i.getSize()).orElse(0L), //скрывается null при неудалении ненайденного элемента
                                addFileToChildrenString(present_parent.getChildren(), i.getId()));
                        // updating next FOLDERS on tree
                        for (Map.Entry<String, Long> pair : present_tree.entrySet()) {
                            folderRepository.updateParentFolder(
                                    pair.getKey(),
                                    longDate,
                                    Optional.ofNullable(pair.getValue()).orElse(0L) + Optional.ofNullable(i.getSize()).orElse(0L)
                            );
                        }
                    }

                    fileRepository.setItem(i.getId(),
                            i.getUrl(),
                            longDate,
                            i.getParentId(),
                            i.getSize());
                }

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
                Optional<SystemItemFolder> past_item = folderRepository.getById(i.getId());
                if (past_item.isPresent()){
                    if (past_item.get().getParentId().equals(i.getParentId())){ //если родители совпадают
                        if (!past_item.get().getUrl().equals(i.getUrl())){
                            folderRepository.save(new SystemItemFolder(i.getId(),i.getUrl(),longDate,i.getParentId(),past_item.get().getSize(),past_item.get().getChildren()));
                        }
                    }
                    else { //если отличаются
                        HashMap<String,Long> past_tree = treeOfParentWithSize(past_item.get().getParentId());
                        HashMap<String,Long> present_tree = treeOfParentWithSize(i.getParentId());

                        if (!past_tree.isEmpty()){ //перепись размера у старого древа размеров - прошлый размер
                            // updating Mother FOLDER
                            SystemItemFolder past_parent = getParent(past_item.get().getParentId());
                            folderRepository.updateParentFolderAndAddItemToChildren(
                                    past_item.get().getParentId(),
                                    longDate,
                                    Optional.ofNullable(past_tree.remove(past_item.get().getParentId())).orElse(0L)-Optional.ofNullable(past_item.get().getSize()).orElse(0L), //скрывается null при неудалении ненайденного элемента
                                    removeFileFromChildrenString(past_parent.getChildren(), past_item.get().getId()));
                            // updating next FOLDERS on tree
                            for (Map.Entry<String, Long> pair : past_tree.entrySet()) {
                                folderRepository.updateParentFolder(
                                        pair.getKey(),
                                        longDate,
                                        Optional.ofNullable(pair.getValue()).orElse(0L) - Optional.ofNullable(past_item.get().getSize()).orElse(0L)
                                );
                            }
                        }

                        if (!present_tree.isEmpty()){ //запись нового древа размер + новый размер
                            // updating Mother FOLDER
                            SystemItemFolder present_parent = getParent(i.getParentId());
                            folderRepository.updateParentFolderAndAddItemToChildren(
                                    i.getParentId(),
                                    longDate,
                                    Optional.ofNullable(present_tree.remove(i.getParentId())).orElse(0L)+Optional.ofNullable(past_item.get().getSize()).orElse(0L), //скрывается null при неудалении ненайденного элемента
                                    addFileToChildrenString(present_parent.getChildren(), i.getId()));
                            // updating next FOLDERS on tree
                            for (Map.Entry<String, Long> pair : present_tree.entrySet()) {
                                folderRepository.updateParentFolder(
                                        pair.getKey(),
                                        longDate,
                                        Optional.ofNullable(pair.getValue()).orElse(0L) + Optional.ofNullable(i.getSize()).orElse(0L)
                                );
                            }
                        }
                        folderRepository.save(new SystemItemFolder(i.getId(),i.getUrl(),longDate,i.getParentId(),past_item.get().getSize(),past_item.get().getChildren()));
                    }
                }
                else { //Если папки нет в таблице
                    SystemItemFolder present_parent = getParent(i.getParentId());
                    if (present_parent != null){
                        folderRepository.updateParentFolderAndAddItemToChildren( present_parent.getId(), longDate, present_parent.getSize(), addFileToChildrenString(present_parent.getChildren(),i.getId()) );
                    }
                    folderRepository.setItem(i.getId(), i.getUrl(), longDate, i.getParentId(), null, null);
                }
            }
            else throw new SystemItemImportException(String.format("Unknown insert item type %s", i.getType()));


        }
        return true;
    }

    /*private void updatingSystemItemInF (SystemItemImport i, long longDate, SystemItem past_item){
        SystemItem currentItem;
        if (i.getType() == SystemItemImport.SystemItemType.FILE) {
            currentItem = new SystemItem(i.getId(),i.getUrl(),longDate,i.getParentId(),i.getType(),i.getSize(),null);

        } else if (i.getType() == SystemItemImport.SystemItemType.FOLDER) {
                currentItem = new SystemItem(i.getId(), i.getUrl(), longDate, i.getParentId(), i.getType(), past_item.getSize(), past_item.getChildren());
        }
        HashMap<String,Long> parentTreeCurrent = treeOfParentWithSize (i.getParentId());
    }*/

    /*@Transactional
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
    }*/

    private SystemItemFolder getParent (String parentId){
        if (parentId == null){
            return null;
        }
        return folderRepository.getById(parentId)
                .orElseThrow(()->new SystemItemImportException(
                        String.format("ParentId %s not found", parentId)));
    }

    private HashMap<String, Long> treeOfParentWithSize (String parentId){
        HashMap<String, Long> parentTree = new HashMap<String, Long>();
        while (parentId != null){
            String finalParentId = parentId;
            SystemItemFolder parent = folderRepository.getById(parentId)
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
        return Arrays.asList(children.split(",")).contains(itemId);
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
