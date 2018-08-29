import org.apache.commons.lang3.CharSet;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.client.methods.HttpPropfind;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.List;

public class FileAttributeTest {
    @Test
    public void test() throws IOException {
        String url = "C:\\Users\\Administrator\\Downloads\\test1.xml";
        System.out.println(Files.getAttribute(Paths.get(url),"basic:creationTime"));
        UserDefinedFileAttributeView userDefinedFileAttributeView = Files.getFileAttributeView(Paths.get(url), UserDefinedFileAttributeView.class);
        ByteBuffer byteBuffer = ByteBuffer.wrap("1".getBytes());
        //userDefinedFileAttributeView.write("version",byteBuffer);
        List<String> attrNames = userDefinedFileAttributeView.list(); // 读出所有属性
        for (String name: attrNames) {
            ByteBuffer bb = ByteBuffer.allocate(userDefinedFileAttributeView.size(name)); // 准备一块儿内存块读取
            userDefinedFileAttributeView.read(name, bb);
            bb.flip();
            String value = Charset.defaultCharset().decode(bb).toString();
            System.out.println(name + " : " + value);
        }

        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("xxxx", "xxxx");
        UsernamePasswordCredentials credentials1 = new UsernamePasswordCredentials("admin", "admin");
        UsernamePasswordCredentials credentials2 = new UsernamePasswordCredentials("806784568@qq.com", "acwqgpgqtn9pz3ya");
        provider.setCredentials(AuthScope.ANY,credentials2);

        //initClient
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultCredentialsProvider(provider);
        CloseableHttpClient client = builder.build();

        try {
            HttpPut httpPut=new HttpPut("https://dav.jianguoyun.com/dav/NoteCloud/test1.xml");
            httpPut.setEntity(new FileEntity(new File("D:\\JavaProject\\NoteCloud\\documents\\documents.xml")));

            CloseableHttpResponse response = client.execute(httpPut);
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
}
