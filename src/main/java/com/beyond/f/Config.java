package com.beyond.f;

import com.beyond.MainApplication;
import com.beyond.utils.ConfigUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Config {

    public final static Logger logger = LogManager.getLogger();

    public final static String COMMIT_STRING_NOTE = "note\n";
    public final static String COMMIT_STRING_TODO = "todo\n";
    public final static String COMMIT_STRING_END = "end\n";

    public static String DEFAULT_XML_PATH ;
    public static String DELETED_XML_PATH ;

    public static String DEFAULT_REMOTE_URL;

    public static String USERNAME ;
    public static String PASSWORD ;

    public static Long SYNCHRONIZE_PERIOD;
    public static String DOWNLOAD_TMP_PATH;

    static {
        refreshConfig();
    }

    public static void refreshConfig() {
        DEFAULT_XML_PATH = ConfigUtils.getProperty("dao.xmlPath.default");
        DELETED_XML_PATH = ConfigUtils.getProperty("dao.xmlPath.deleted");

        DEFAULT_REMOTE_URL = ConfigUtils.getProperty("remote.url.default");

        USERNAME = ConfigUtils.getProperty("remote.username");
        PASSWORD = ConfigUtils.getProperty("remote.password");

        SYNCHRONIZE_PERIOD = Long.parseLong(ConfigUtils.getProperty("remote.synchronize.period"));

        DOWNLOAD_TMP_PATH = ConfigUtils.getProperty("remote.download.tmpPath");
    }

    public static void saveOrUpdateConfigProperty(String propertyName, String value) {
        ConfigUtils.setProperty(propertyName, value);
        ConfigUtils.storeProperties();
    }

}
