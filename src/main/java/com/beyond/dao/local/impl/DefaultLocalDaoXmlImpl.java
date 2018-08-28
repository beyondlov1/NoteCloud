package com.beyond.dao.local.impl;

import com.beyond.utils.ConfigUtils;

public class DefaultLocalDaoXmlImpl extends LocalDaoXmlImpl {

    public DefaultLocalDaoXmlImpl(){
        super(ConfigUtils.getProperty("dao.xmlPath.default"));
    }
}
