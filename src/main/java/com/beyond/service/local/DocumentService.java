package com.beyond.service.local;

import com.beyond.entity.Document;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;

public interface DocumentService extends BaseService<Document>{
    ObservableList<com.beyond.entity.fx.Document> initObservableList();

    void initTableView(TableView<com.beyond.entity.fx.Document> tableView, ObservableList<com.beyond.entity.fx.Document> list);

    void initWebView(WebView webView, com.beyond.entity.fx.Document newValue);

    void initTableColumn(TableColumn<com.beyond.entity.fx.Document, String> tableColumn, String propertyName);

    void initListView(ListView listView);
    void initText(Text text);



    void add(Document document, ObservableList<com.beyond.entity.fx.Document> fxDocumentList);

    void delete(Document document, ObservableList<com.beyond.entity.fx.Document> fxDocumentList);

    void deleteById(String id, ObservableList<com.beyond.entity.fx.Document> fxDocumentList);

    void update(Document document, ObservableList<com.beyond.entity.fx.Document> fxDocumentList);
}
