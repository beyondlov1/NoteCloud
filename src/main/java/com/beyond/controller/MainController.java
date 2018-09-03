package com.beyond.controller;

import com.beyond.entity.Note;
import com.beyond.entity.Todo;
import com.beyond.entity.fx.Document;
import com.beyond.f.Config;
import com.beyond.service.local.DocumentService;
import com.beyond.service.local.impl.DocumentServiceImpl;
import com.beyond.service.remote.RemoteDocumentService;
import com.beyond.service.remote.impl.MergeRemoteDocumentServiceImpl;
import com.beyond.service.remote.impl.SimpleRemoteDocumentServiceImpl;
import com.beyond.utils.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import javafx.util.Duration;

import javax.naming.TimeLimitExceededException;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Predicate;

import static com.beyond.f.Config.SYNCHRONIZE_PERIOD;
import static com.beyond.f.Config.logger;
import static com.beyond.service.remote.impl.MergeRemoteDocumentServiceImpl.mergeFxDocuments;

public class MainController {

    private DocumentService documentService = new DocumentServiceImpl(Config.DEFAULT_XML_PATH);
    private DocumentService deletedDocumentService = new DocumentServiceImpl(Config.DELETED_XML_PATH);
    private RemoteDocumentService remoteDocumentService = new SimpleRemoteDocumentServiceImpl(Config.DEFAULT_REMOTE_URL, Config.DEFAULT_XML_PATH);
    private RemoteDocumentService remoteDocumentService1 = new MergeRemoteDocumentServiceImpl(Config.DEFAULT_REMOTE_URL, Config.DEFAULT_XML_PATH);
    private Timer timer = new Timer();
    private Timeline timeline;

    private String selectedId;

    //添加组件
    @FXML
    private Text message;

    @FXML
    private TextField titleTextField;

    @FXML
    private TextArea contentTextAreaSaveOrUpdate;

    @FXML
    private TextArea contentTextAreaSave;


    //展示组件
    @FXML
    private HBox container;

    @FXML
    private TableView<Document> documentTableView;

    @FXML
    private TableView<Document> deletedDocumentTableView;

    @FXML
    private WebView webView;

    @FXML
    private TabPane tabPane;

    @FXML
    private TableColumn<Document, String> contentTableColumn;

    @FXML
    private TableColumn<Document, String> deletedContentTableColumn;


    private ObservableList<Document> fxDocumentList = null;
    private ObservableList<Document> deletedFxDocumentList = null;

    public enum SortType {
        ASC, DESC
    }

    @FXML
    private void initialize() {
        init();
    }

    private void init() {
        fxDocumentList = documentService.initObservableList();
        documentService.initTableColumn(contentTableColumn, "content");
        documentService.initTableView(documentTableView, fxDocumentList);
        initStyles();
        initListeners();

        deletedFxDocumentList = deletedDocumentService.initObservableList();
        deletedDocumentService.initTableColumn(deletedContentTableColumn, "content");
        deletedDocumentService.initTableView(deletedDocumentTableView, deletedFxDocumentList);
        initStyles();
        initListeners();

        initFocus();
    }

    private void initListeners() {
        container.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode code = event.getCode();
                switch (code) {
                    case E:
                        documentTableView.requestFocus();
                        documentTableView.getSelectionModel().selectPrevious();
                        documentTableView.scrollTo(documentTableView.getSelectionModel().getSelectedIndex());
                        break;
                    case D:
                        documentTableView.requestFocus();
                        documentTableView.getSelectionModel().selectNext();
                        documentTableView.scrollTo(documentTableView.getSelectionModel().getSelectedIndex());
                        break;
                    default:
                        break;
                }
            }
        });
        documentTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<com.beyond.entity.fx.Document>() {
            @Override
            public void changed(ObservableValue<? extends com.beyond.entity.fx.Document> observable, com.beyond.entity.fx.Document oldValue, com.beyond.entity.fx.Document newValue) {
                if (newValue != null) {
                    documentService.initWebView(webView, newValue);
                    contentTextAreaSaveOrUpdate.setText(newValue.getContent());
                    selectedId = newValue.getId();
                }
            }
        });
        deletedDocumentTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<com.beyond.entity.fx.Document>() {
            @Override
            public void changed(ObservableValue<? extends com.beyond.entity.fx.Document> observable, com.beyond.entity.fx.Document oldValue, com.beyond.entity.fx.Document newValue) {
                deletedDocumentService.initWebView(webView, newValue);
                contentTextAreaSaveOrUpdate.setText(newValue.getContent());
                selectedId = newValue.getId();
            }
        });
        documentTableView.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode code = event.getCode();
                switch (code) {
                    case DELETE:
                        delete();
                        break;
                    case I:
                        contentTextAreaSave.requestFocus();
                        changeInputMethod();
                        break;
                    case U:
                    case LEFT:
                        contentTextAreaSaveOrUpdate.requestFocus();
                        contentTextAreaSaveOrUpdate.positionCaret(contentTextAreaSaveOrUpdate.getText().length());
                        changeInputMethod();
                        break;
                    default:
                        break;
                }
            }
        });
        /**
         * OnInputMethodTextChanged这个方法是用来检测输入法是否发生变化, 这里用它来放置tableView获得焦点后, 切换输入法, 再切换到
         * 别的节点时再次切换输入法的问题, 这样可以保证tableView一直是无输入法的状态(或者是默认输入法??没有试)
         */
        documentTableView.setOnInputMethodTextChanged(new EventHandler<InputMethodEvent>() {
            @Override
            public void handle(InputMethodEvent event) {
                if (documentTableView.isFocused()) {
                    changeInputMethod();
                }
            }
        });
        contentTextAreaSave.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                focusBackToTableView(event);
            }
        });
        contentTextAreaSaveOrUpdate.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                focusBackToTableView(event);
            }
        });

//        fxDocumentList.addListener(new ListChangeListener<Document>() {
//            @Override
//            public void onChanged(Change<? extends Document> c) {
//                synchronizeModelAndView();
//            }
//        });
    }

    /**
     * Robot这个类要用awt包中的, fxRobox不知道为什么不好使
     * 暂时只能用这种来模拟按键的方式来切换输入法, 还没有发现更好的办法...
     */
    private void changeInputMethod() {
        Robot robot = null;
        try {
            robot = new Robot();
            robot.keyPress(java.awt.event.KeyEvent.VK_CONTROL);
            robot.keyPress(java.awt.event.KeyEvent.VK_SPACE);
            robot.keyRelease(java.awt.event.KeyEvent.VK_CONTROL);
            robot.keyRelease(java.awt.event.KeyEvent.VK_SPACE);
        } catch (AWTException e) {
            e.printStackTrace();
        }

    }

    private void focusBackToTableView(KeyEvent event) {
        KeyCode code = event.getCode();
        switch (code) {
            case ESCAPE:
                documentTableView.requestFocus();
                changeInputMethod();
                break;
            default:
                break;
        }
    }

    private void initStyles() {
        contentTextAreaSaveOrUpdate.setWrapText(true);
        contentTextAreaSave.setWrapText(true);
    }

    private void initFocus() {
        contentTextAreaSaveOrUpdate.setFocusTraversable(false);
        contentTextAreaSave.setFocusTraversable(false);
        tabPane.setFocusTraversable(false);
        documentTableView.requestFocus();
        documentTableView.getSelectionModel().select(0);
    }


    @FXML
    private void save(KeyEvent keyEvent) {
        //清空message
        message.setText(null);
        com.beyond.entity.Document document = validate(contentTextAreaSave);
        //提交数据
        if (document != null) {
            //设置版本
            document.setVersion(1);
            //持久化
            documentService.add(document, fxDocumentList);
            changeViewAfterSave(contentTextAreaSave);
        }
    }

    @FXML
    private void saveOrUpdate(KeyEvent keyEvent) {
        //清空message
        message.setText(null);

        com.beyond.entity.Document document = validate(contentTextAreaSaveOrUpdate);

        //提交数据
        if (document != null) {
            //持久化
            if (StringUtils.isEmpty(selectedId)) {
                documentService.add(document, fxDocumentList);
                changeViewAfterSave(contentTextAreaSaveOrUpdate);
            } else {
                //设置版本
                com.beyond.entity.Document foundDocument = documentService.findById(selectedId);
                foundDocument.setVersion(foundDocument.getVersion() + 1);
                foundDocument.setContent(document.getContent());
                documentService.update(foundDocument, fxDocumentList);
                refresh();
                //設置文字和光標
                contentTextAreaSaveOrUpdate.setText(document.getContent());
                contentTextAreaSaveOrUpdate.positionCaret(document.getContent().length());
                changeViewAfterUpdate(contentTextAreaSaveOrUpdate);
            }
        }
    }

    @FXML
    private void delete() {
        if (!StringUtils.isEmpty(selectedId)) {
            Document document = ListUtils.getFxDocumentById(fxDocumentList, selectedId);
            Document deletedDocument = ListUtils.getFxDocumentById(deletedFxDocumentList, selectedId);
            if (document != null) {
                documentService.deleteById(selectedId, fxDocumentList);
            }
            if (deletedDocument != null) {
                deletedDocumentService.deleteById(selectedId, deletedFxDocumentList);
            }
            changeViewAfterDelete();
        }
    }

    private void changeViewAfterDelete() {
        if (documentTableView.getSelectionModel().getSelectedIndex() != 0) {
            documentTableView.getSelectionModel().selectNext();
        }
    }

    private void changeViewAfterSave(TextArea textArea) {
        //clear
        textArea.setText(null);
        documentTableView.requestFocus();
        documentTableView.getSelectionModel().select(0);
        documentTableView.scrollTo(0);
        changeInputMethod();
    }

    private void changeViewAfterUpdate(TextArea textArea) {
        documentTableView.requestFocus();
        changeInputMethod();
    }

    private com.beyond.entity.Document validate(TextArea contentTextArea) {

        //获取数据
        String content = contentTextArea.getText();
        String type = null;

        //结尾输入end加回车的的时候保存并结束,不会包括end
        boolean isCommit = false;
        String[] targetEndStringArray = new String[]{Config.COMMIT_STRING_NOTE, Config.COMMIT_STRING_END, Config.COMMIT_STRING_TODO};

        //判断能否提交
        for (String targetEndString : targetEndStringArray) {
            String endString = null;
            int targetEndStringLength = targetEndString.length();
            if (content != null && content.length() >= targetEndStringLength) {
                endString = content.substring(content.length() - targetEndStringLength);
                isCommit = (targetEndString).equals(endString);
                switch (targetEndString) {
                    case Config.COMMIT_STRING_NOTE:
                        type = "note";
                        break;
                    case Config.COMMIT_STRING_TODO:
                        type = "todo";
                        break;
                    case Config.COMMIT_STRING_END:
                        type = null;
                        break;
                    default:
                        break;
                }
                if (isCommit) {
                    content = content.substring(0, content.length() - targetEndStringLength);
                    break;
                }
            }
        }

        if (isCommit) {
            //封装数据
            com.beyond.entity.Document document = null;
            if (org.apache.commons.lang3.StringUtils.isNotBlank(type)) {
                switch (type) {
                    case "note":
                        document = new Note();
                        document.setContent(content);
                        document.setVersion(1);
                        document.setType(type);
                        break;
                    case "todo":
                        document = new Todo();
                        document.setContent(content);
                        document.setVersion(1);
                        document.setType(type);
                        Date remindDate = TimeUtils.parse(content);
                        if (remindDate != null) {
                            ((Todo) document).setRemindDate(remindDate);
                        }
                        break;
                    default:
                        break;
                }
            } else {
                document = new com.beyond.entity.Document();
                document.setContent(content);
                document.setVersion(1);
            }

            if (document.isValid()) {
                return document;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private void refresh() {
        documentTableView.refresh();

        if (selectedId!=null){
            com.beyond.entity.Document selectedDocument = documentService.findById(selectedId);
            if (selectedDocument==null) return;
            documentService.initWebView(webView, new Document(documentService.findById(selectedId)));
        }
    }

    private void changeWorkSpace(String xmlPathPropertyName) {
        documentService.changeWorkSpace(xmlPathPropertyName);
    }

    private void synchronizeModelAndView() {
        fxDocumentList = mergeFxDocuments==null?documentService.initObservableList():mergeFxDocuments;
        documentService.initTableView(documentTableView,fxDocumentList);
        com.beyond.entity.Document selectedDocument = documentService.findById(selectedId);
        if (selectedDocument==null) selectedId = null;
        documentTableView.getSelectionModel().select(selectedId == null ? 0 : ListUtils.getFxDocumentIndexById(fxDocumentList, selectedId));
        refresh();
    }

    public void startSynchronize() {
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                Platform.runLater(() -> {
//                    try {
//                        remoteDocumentService.synchronize(new Callback<SimpleRemoteDocumentServiceImpl.SynchronizeType, Object>() {
//                            @Override
//                            public Object call(SimpleRemoteDocumentServiceImpl.SynchronizeType param) {
//                                if (param== SimpleRemoteDocumentServiceImpl.SynchronizeType.DOWNLOAD) {
//                                    synchronizeModelAndView();
//                                }
//                                return null;
//                            }
//                        });
//                        logger.info("synchronize success");
//                    }catch (Exception e){
//                        e.printStackTrace();
//                        logger.info("connect timeout");
//                    }
//                });
//            }
//        }, 1000, SYNCHRONIZE_PERIOD*60*1000);

//         timeline = new Timeline(new KeyFrame(Duration.seconds(SYNCHRONIZE_PERIOD*60), new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                try {
//                    remoteDocumentService.synchronize(new Callback<SimpleRemoteDocumentServiceImpl.SynchronizeType, Object>() {
//                        @Override
//                        public Object call(SimpleRemoteDocumentServiceImpl.SynchronizeType param) {
//                            if (param== SimpleRemoteDocumentServiceImpl.SynchronizeType.DOWNLOAD) {
//                                synchronizeModelAndView();
//                            }
//                            return null;
//                        }
//                    });
//                    logger.info("synchronize success");
//                }catch (Exception e){
//                    e.printStackTrace();
//                    logger.info("connect timeout");
//                }
//            }
//        }));
//        timeline.setCycleCount(Animation.INDEFINITE);
//        timeline.play();


        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                remoteDocumentService1.synchronize(null);
            }
        };
        timer.schedule(timerTask, 0, SYNCHRONIZE_PERIOD*60 * 1000);

        timeline = new Timeline(new KeyFrame(Duration.seconds(SYNCHRONIZE_PERIOD*5), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                synchronizeModelAndView();
            }
        }));
        timeline.setDelay(new Duration(10000));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }

    public void stopSynchronize() {
        timer.cancel();
       timeline.stop();
    }
}


