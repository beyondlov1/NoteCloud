package com.beyond.utils;

import com.beyond.entity.fx.Document;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {
    public static void deleteDocumentById(List<Document> list,String id){
        int index = getFxDocumentIndexById(list,id);
        if (index!=-1){
            list.remove(index);
        }
    }

    public static Document getFxDocumentById(List<Document> list, String id){
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(id)){
                index = i;
            }
        }
        if (index==-1){
            return null;
        }
        return list.get(index);
    }

    public static com.beyond.entity.Document getDocumentById(List<com.beyond.entity.Document> list, String id){
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(id)){
                index = i;
            }
        }
        if (index==-1){
            return null;
        }
        return list.get(index);
    }



    public static int getFxDocumentIndexById(List<Document> list, String id){
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(id)){
                index = i;
            }
        }
        return index;
    }

    public static ObservableList<Document> getFxDocumentListFromDocumentList(List<com.beyond.entity.Document> mergeList) {
        List<Document> list = new ArrayList<>();
        for (com.beyond.entity.Document document: mergeList){
            list.add(new Document(document));
        }
        return FXCollections.observableList(list);
    }
}
