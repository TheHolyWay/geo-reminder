package ru.holyway.georeminder;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.holyway.georeminder.nlp.util.Lemmatizer;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NlpTests {

    @Autowired
    private Lemmatizer lemmatizer;

    @Test
    public void testLemmatizer() {
        Assert.assertEquals("пятница", lemmatizer.resolveLemma("пятницу"));

    }
}
