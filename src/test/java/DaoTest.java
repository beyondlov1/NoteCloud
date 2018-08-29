import com.beyond.dao.local.LocalDao;
import com.beyond.dao.local.impl.LocalDaoXmlImpl;
import com.beyond.dao.remote.RemoteDao;
import com.beyond.dao.remote.impl.SimpleRemoteDaoImpl;
import com.beyond.f.F;
import org.junit.Test;

import java.util.Date;

public class DaoTest {
    @Test
    public void test(){
        LocalDao localDao = new LocalDaoXmlImpl("D:\\JavaProject\\NoteCloud\\documents\\documents.xml");
        //localDao.setVersion(3);
        int version = localDao.getVersion();
        long lastModifyTime = localDao.getLastModifyTime();
        System.out.println(new Date(lastModifyTime));
    }

    @Test
    public void test1(){
        String url = "https://dav.jianguoyun.com/dav/NoteCloud/test1.xml";
        RemoteDao remoteDao = new SimpleRemoteDaoImpl(url);
        remoteDao.setVersion(9);
        int version = remoteDao.getVersion();
        System.out.println(version);
    }
}
