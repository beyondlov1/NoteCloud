package com.beyond.f;

import com.beyond.utils.ConfigUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.simple.SimpleLogger;

public interface F {

    Logger logger = LogManager.getLogger();

    String DEFAULT_XML_PATH = ConfigUtils.getProperty("dao.xmlPath.default");
    String DELETED_XML_PATH = ConfigUtils.getProperty("dao.xmlPath.deleted");

    String DEFAULT_REMOTE_URL = ConfigUtils.getProperty("remote.url.default");

    String COMMIT_STRING_NOTE = "note\n";
    String COMMIT_STRING_TODO = "todo\n";
    String COMMIT_STRING_END = "end\n";

    String USERNAME = ConfigUtils.getProperty("remote.username");
    String PASSWORD = ConfigUtils.getProperty("remote.password");
}
