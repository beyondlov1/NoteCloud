package com.beyond.utils;

import com.beyond.entity.fx.Document;
import javafx.collections.ObservableList;

import java.util.List;

public class ListUtils {
    public static void deleteDocumentById(List<Document> list,String id){
        int index = getDocumentIndexById(list,id);
        if (index!=-1){
            list.remove(index);
        }
    }

    public static Document getDocumentById(List<Document> list,String id){
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(id)){
                index = i;
            }
        }
        return list.get(index);
    }

    private static int getDocumentIndexById(List<Document> list,String id){
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(id)){
                index = i;
            }
        }
        return index;
    }
}
