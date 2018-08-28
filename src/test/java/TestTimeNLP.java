import com.time.nlp.TimeNormalizer;
import com.time.nlp.TimeUnit;
import org.junit.Test;

import java.net.URISyntaxException;
import java.net.URL;

public class TestTimeNLP {

    @Test
    public void test() throws URISyntaxException {
        URL url = TimeNormalizer.class.getResource("/TimeExp.m");
        System.out.println(url.toURI().toString());
        TimeNormalizer normalizer = new TimeNormalizer(url.toURI().toString());
        normalizer.setPreferFuture(true);

        TimeUnit[] timeUnits = normalizer.parse("明天晚上九点");
        System.out.println(timeUnits[0].getTime());
    }
}
