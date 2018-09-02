package com.beyond.service.local.impl;

import com.beyond.controller.MainController;
import com.beyond.dao.local.LocalDao;
import com.beyond.entity.Document;
import com.beyond.proxy.LocalDaoProxy;
import com.beyond.service.local.DocumentService;
import com.beyond.utils.*;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.beyond.f.Config.DELETED_XML_PATH;

public class DocumentServiceImpl implements DocumentService {

    private LocalDao localDao;
    private String xmlPath;

    public DocumentServiceImpl(String xmlPath){
        changeWorkSpace(xmlPath);
    }

    public void changeWorkSpace(String xmlPath){
        this.xmlPath = xmlPath;
        this.localDao = LocalDaoProxy.getInstance().getLocalDao(xmlPath);
    }

    @Override
    public ObservableList<com.beyond.entity.fx.Document> initObservableList(){
        List<com.beyond.entity.Document> documents = this.findAll();
        List<com.beyond.entity.fx.Document> fxDocuments = new ArrayList<>();
        for (com.beyond.entity.Document document : documents) {
            fxDocuments.add(new com.beyond.entity.fx.Document(document));
        }
        ObservableList<com.beyond.entity.fx.Document> result = FXCollections.observableList(fxDocuments);
        SortUtils.sort(result,com.beyond.entity.fx.Document.class,"lastModifyTime",MainController.SortType.DESC);
        return result;
    }

    @Override
    public void initTableView(TableView<com.beyond.entity.fx.Document> tableView, ObservableList<com.beyond.entity.fx.Document> list) {
        tableView.setItems(list);
    }

    @Override
    public void initWebView(WebView webView, com.beyond.entity.fx.Document newValue) {
        String content = newValue.getContent();

        //markdownToHtml
        content = StringUtils.replaceUrlsToMarkDownStyle(content);
        content = MarkDownUtils.convertMarkDownToHtml(content);

        //add css
        content = HtmlUtils.addGithubMarkDownCss(content);
        webView.getEngine().loadContent(content);
    }

    @Override
    public void initTableColumn(TableColumn<com.beyond.entity.fx.Document, String> tableColumn, String propertyName) {
        tableColumn.setCellFactory(new Callback<TableColumn<com.beyond.entity.fx.Document, String>, TableCell<com.beyond.entity.fx.Document, String>>() {
            @Override
            public TableCell<com.beyond.entity.fx.Document, String> call(TableColumn<com.beyond.entity.fx.Document, String> param) {
                return new TableCell<com.beyond.entity.fx.Document, String>(){
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            String content = HtmlUtils.parseHtml2Text(item);
                            content = StringUtils.cutAndPretty(content,100);
                            Text text = new Text(content);
                            TextFlow textFlow = new TextFlow(text);
                            textFlow.setPadding(new Insets(5,10,5,10));
                            textFlow.setPrefHeight(40);
                            textFlow.setMaxHeight(100);
                            setGraphic(textFlow);
                        }
                    }
                };
            }
        });
        tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<com.beyond.entity.fx.Document, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<com.beyond.entity.fx.Document, String> param) {
                Property valueByField = (Property) ReflectUtils.getValueByField(com.beyond.entity.fx.Document.class, param.getValue(), propertyName);
                return valueByField;
            }
        });
    }

    @Override
    public void initListView(ListView listView) {

    }

    @Override
    public void initText(Text text) {

    }







    @Override
    public List<Document> findAll() {
        return localDao.selectAll();
    }

    @Override
    public Document findById(String id) {
        return localDao.selectById(id);
    }

    @Override
    public void add(Document document) {
        document.setId(IdUtils.getId());
        Date createTime = TimeUtils.getCurrentDate();
        document.setCreateTime(createTime);
        document.setLastModifyTime(createTime);
        localDao.add(document);
    }

    @Override
    public void add(Document document, ObservableList<com.beyond.entity.fx.Document> fxDocumentList){
        add(document);
        fxDocumentList.add(0, new com.beyond.entity.fx.Document(document));
    }

    @Override
    public void delete(Document document) {
        deleteById(document.getId());
    }

    @Override
    public void delete(Document document, ObservableList<com.beyond.entity.fx.Document> fxDocumentList) {
        deleteById(document.getId(),fxDocumentList);
    }

    @Override
    public void deleteById(String id, ObservableList<com.beyond.entity.fx.Document> fxDocumentList) {
        deleteById(id);
        ListUtils.deleteDocumentById(fxDocumentList, id);
    }

    @Override
    public void deleteById(String id){
        Document document = null;
        document = localDao.selectById(id);
        if (!DELETED_XML_PATH.equals(xmlPath)){
            localDao.setXmlPath(DELETED_XML_PATH);
            localDao.add(document);
        }
        localDao.setXmlPath(xmlPath);
        localDao.delete(document);
    }

    @Override
    public void update(Document document) {
        document.setLastModifyTime(TimeUtils.getCurrentDate());
        localDao.update(document);

    }

    @Override
    public void update(Document document,ObservableList<com.beyond.entity.fx.Document> fxDocumentList){
        update(document);
        com.beyond.entity.fx.Document foundDocument = ListUtils.getFxDocumentById(fxDocumentList, document.getId());
        foundDocument.setDocument(document);
    }



}
