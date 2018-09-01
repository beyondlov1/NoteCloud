import com.beyond.dao.local.LocalDao;
import com.beyond.dao.local.impl.LocalDaoXmlImpl;
import com.beyond.dao.remote.RemoteDao;
import com.beyond.dao.remote.impl.SimpleRemoteDaoImpl;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class DaoTest {
    @Test
    public void test(){
        LocalDao localDao = new LocalDaoXmlImpl("Config:\\git_repository\\MyGitHub\\NoteCloud\\NoteCloud\\documents\\downloadDocuments.xml");
        //localDao.setVersion(3);
//        int version = localDao.getVersion();
//        long lastModifyTime = localDao.getLastModifyTimeMills();
//        System.out.println(new Date(lastModifyTime));
        Map<String,Object> map = new HashMap<>();
        map.put("test1","test1");
        map.put("test2","test2");
        map.put("test3","test3");
        localDao.setProperties(map);
        Map<String, Object> properties = localDao.getProperties();
        System.out.println(properties);
    }

    @Test
    public void test1(){
        String url = "https://dav.jianguoyun.com/dav/NoteCloud/test1.xml";
        RemoteDao remoteDao = new SimpleRemoteDaoImpl(url);
        remoteDao.setVersion(9);
        String version = remoteDao.getVersion();
        System.out.println(version);
    }
}
