package org.newstand.logger;

import org.junit.Test;

/**
 * Created by Nick@NewStand.org on 2017/4/1 9:59
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class LoggerTest {

    @Test
    public void config() throws Exception {
        Logger.config(Settings.builder()
                .logLevel(0)
                .tag("DataM")
                .build());
    }

    @Test
    public void d() throws Exception {
        config();
        int N = 1000;
        while (N > 0) {
            Logger.d("MESSAGE");
            Logger.d("MESSAGE %s%d", "STRING", 999);
            N--;
        }

        Thread.sleep(20 * 1000);
    }

    @Test
    public void d1() throws Exception {

    }

    @Test
    public void e() throws Exception {

    }

    @Test
    public void e1() throws Exception {

    }

    @Test
    public void i() throws Exception {

    }

    @Test
    public void v() throws Exception {

    }

    @Test
    public void w() throws Exception {

    }

    @Test
    public void wtf() throws Exception {

    }

}