package com.beyond.f;

import com.beyond.utils.ConfigUtils;

public interface F {
    String DEFAULT_XML_PATH = ConfigUtils.getProperty("dao.xmlPath.default");
    String DELETED_XML_PATH = ConfigUtils.getProperty("dao.xmlPath.deleted");

    String COMMIT_STRING_NOTE = "note\n";
    String COMMIT_STRING_TODO = "todo\n";
    String COMMIT_STRING_END = "end\n";
}
