package com.beyond.dao.local.impl;

import com.beyond.utils.ConfigUtils;

public class DeletedLocalDaoXmlImpl extends LocalDaoXmlImpl {

    public DeletedLocalDaoXmlImpl() {
        super(ConfigUtils.getProperty("dao.xmlPath.deleted"));
    }
}
