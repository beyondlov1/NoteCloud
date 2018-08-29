package com.beyond.dao.remote.synchronize.simple.impl;

import com.beyond.dao.remote.synchronize.simple.Synchroinze;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.client.methods.HttpPropfind;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.PropfindInfo;
import org.apache.jackrabbit.webdav.xml.Namespace;

import java.io.IOException;
import java.io.InputStream;

public class SynchronizeImpl implements Synchroinze {


    private CloseableHttpClient getClient(){
        //init credentials
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("xxxx", "xxxx");
        UsernamePasswordCredentials credentials1 = new UsernamePasswordCredentials("admin", "admin");
        UsernamePasswordCredentials credentials2 = new UsernamePasswordCredentials("xxxx", "xxxx");
        provider.setCredentials(AuthScope.ANY,credentials1);

        //initClient
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultCredentialsProvider(provider);
        return builder.build();
    }

    @Override
    public void synchronize(String url, String filePath) {
        CloseableHttpClient client = getClient();
        try {
            DavPropertyNameSet set = new DavPropertyNameSet();
            set.add("testprop",Namespace.getNamespace("myNamespace1"));
            HttpPropfind httpPropfind = new HttpPropfind(url,DavConstants.PROPFIND_BY_PROPERTY,set,DavConstants.DEPTH_INFINITY);
            CloseableHttpResponse response = client.execute(httpPropfind);
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            int len = 0;
            byte[] b = new byte[1024];
            while((len=content.read(b))!=-1){
                System.out.println(new String(b,0,len));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args){
        Synchroinze synchroinze = new SynchronizeImpl();
        synchroinze.synchronize("https://yura.teracloud.jp/dav/test.jpg","");
    }
}
