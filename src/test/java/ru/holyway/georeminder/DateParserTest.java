package ru.holyway.georeminder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.holyway.georeminder.datetime.DefaultTimeExtractionService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DateParserTest {

    @Autowired
    DefaultTimeExtractionService service;

    @Test
    public void test() {
        System.out.println("Завтра вечером: " + service.extractTime("Завтра вечером"));
        System.out.println("вечером в четверг: " + service.extractTime("вечером в четверг"));
        System.out.println("в четверг: " + service.extractTime("в четверг"));
        System.out.println("вечером: " + service.extractTime("вечером"));
    }
}
