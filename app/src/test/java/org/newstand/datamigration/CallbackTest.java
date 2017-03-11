package org.newstand.datamigration;

import org.junit.Test;

/**
 * Created by Nick@NewStand.org on 2017/3/7 12:31
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class CallbackTest {

    @Test
    public void testNoCallback() {

        System.out.println("START!!!");

        Worker worker = new Worker();
        worker.doSomeThing(); // Will block for 10s!!!

        System.out.println("DONE!!!");
    }

    @Test
    public void testWithCallback() {

        Worker worker = new Worker();

        // Won't block!!!!
        worker.doSomeThingAsync(new ListenerAdapter() {
            @Override
            public void onWorkStart() {
                System.out.println("START!!!");
            }

            @Override
            public void onWorkDone() {
                System.out.println("DONE!!!");
            }

            @Override
            public void onWorkError(Exception e) {
                System.out.println("ERROR!!!");
            }
        });

        // So here we can do other things below...
        System.out.println("DOING OTHER THINGS!!!");
    }

}

class Worker {

    // This is async call, will perform in a new thread.
    void doSomeThingAsync(final Listener listener) {
        // Start a thread to call.
        Thread t = new Thread() {
            @Override
            public void run() {
                // Call back!!!
                listener.onWorkStart();

                try {
                    doSomeThing();
                } catch (Exception e) {
                    listener.onWorkError(e);
                }

                // Call back!!!
                listener.onWorkDone();
            }
        };
        t.start();
    }

    void doSomeThing() {
        // Need a long time to complete.
        // We sleep for 10s as an example.
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException ignored) {

        }
    }
}

class ListenerAdapter implements Listener {
    @Override
    public void onWorkStart() {
        // Empty.
    }

    @Override
    public void onWorkDone() {
        // Empty.
    }

    @Override
    public void onWorkError(Exception e) {
        // Empty.
    }

    @Override
    public void onXXXX(String s) {
        // Empty.
    }

    @Override
    public void onYYYY(int i) {
        // Empty.
    }
}

interface Listener {
    void onWorkStart();

    void onWorkDone();

    void onWorkError(Exception e);// Has a param.

    void onXXXX(String s);

    void onYYYY(int i);
}
