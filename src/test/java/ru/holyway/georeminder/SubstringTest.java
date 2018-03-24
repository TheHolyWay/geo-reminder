package ru.holyway.georeminder;

import org.junit.Test;

public class SubstringTest {

    @Test
    public void substringTest() {
        final String full = "ул. Вольская, 1, Саратов, Саратовская обл., Россия, 410056";
        final String[] rqxp = full.split(",");
        System.out.println(rqxp[0] + rqxp[1]);
    }
}
