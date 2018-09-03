package com.beyond.proxy;

import com.beyond.dao.local.LocalDao;
import com.beyond.dao.local.impl.LocalDaoXmlImpl;
import com.beyond.entity.Document;
import com.beyond.f.Config;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Date;

public class LocalDaoProxy {

    private static LocalDaoProxy localDaoProxy;

    public static LocalDaoProxy getInstance() {
        if (localDaoProxy == null) {
            localDaoProxy = new LocalDaoProxy();
        }
        return localDaoProxy;
    }

    public LocalDao getLocalDao(String filePath) {
        final LocalDao localDao = new LocalDaoXmlImpl(filePath);
        return (LocalDao) Proxy.newProxyInstance(localDao.getClass().getClassLoader(), localDao.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String methodName = method.getName();
                if (methodName.startsWith("add") || methodName.startsWith("update") || methodName.startsWith("delete")) {
                    long tmpVersion = localDao.getProperty("_version") == null || "null".equals(localDao.getProperty("_version")) ? 0 : Long.parseLong(localDao.getProperty("_version").toString());
                    Object object = method.invoke(localDao, args);
                    localDao.setProperty("_version", tmpVersion + 1L);
                    localDao.setProperty("_lastModifyTimeMills", new Date().getTime());
                    String modifiedIds =(localDao.getProperty("_modifiedIds") == null ? "" : localDao.getProperty("_modifiedIds")) + (object == null ? "," : ("," + object.toString()));
                    if (StringUtils.length(modifiedIds)>1){
                        modifiedIds=modifiedIds.substring(1);
                    }
                    localDao.setProperty("_modifiedIds", modifiedIds);//如果为空就不会加进去
                    return object;
                } else {
                    return method.invoke(localDao, args);
                }
            }
        });
    }
}
