package dev.tornaco.vangogh;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.AssertionFailedError;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.logger.Logger;

import java.util.concurrent.CountDownLatch;

import dev.tornaco.vangogh.media.ImageSource;
import dev.tornaco.vangogh.display.ImageRequest;
import dev.tornaco.vangogh.request.RequestDispatcher;
import dev.tornaco.vangogh.request.RequestLooper;
import dev.tornaco.vangogh.request.Seq;

/**
 * Created by guohao4 on 2017/8/24.
 * Email: Tornaco@163.com
 */

@RunWith(AndroidJUnit4.class)
public class ImageRequestLooperTest {

    private static final int REQUEST_COUNT = 100;

    private CountDownLatch latch;

    @Before
    public void setup() {
        latch = new CountDownLatch(REQUEST_COUNT);
    }

    @Test(expected = AssertionFailedError.class)
    public void testQuit() {
        RequestLooper requestLooper = RequestLooper.newInstance(new RequestDispatcher() {
            @Override
            public void dispatch(@NonNull ImageRequest request) {
                Assert.fail();
            }

            @Override
            public boolean cancel(@NonNull ImageRequest imageRequest, boolean interruptRunning) {
                return false;
            }

            @Override
            public void cancelAll(boolean interruptRunning) {

            }
        }, Seq.FIFO);
        requestLooper.quitSafely();
        requestLooper.onNewRequest(ImageRequest.builder().build());
    }

    @Test
    public void testSeqFIFO() throws InterruptedException {

        RequestLooper requestLooper = RequestLooper.newInstance(new DummyDispatch(), Seq.FIFO);

        for (int i = 0; i < REQUEST_COUNT; i++) {
            ImageRequest r = ImageRequest.builder().requestTimeMills(System.currentTimeMillis())
                    .alias("r-" + i)
                    .imageSource(ImageSource.builder().url("xxx").build()).build();
            requestLooper.onNewRequest(r);
        }

        latch.await();
    }

    @Test
    public void testSeqFILO() throws InterruptedException {

        RequestLooper requestLooper = RequestLooper.newInstance(new DummyDispatch(), Seq.FILO);

        for (int i = 0; i < REQUEST_COUNT; i++) {
            ImageRequest r = ImageRequest.builder().requestTimeMills(System.currentTimeMillis())
                    .alias("r-" + i)
                    .imageSource(ImageSource.builder().url("xxx").build()).build();
            requestLooper.onNewRequest(r);
        }

        latch.await();
    }


    class DummyDispatch implements RequestDispatcher {
        @Override
        public void dispatch(@NonNull ImageRequest imageRequest) {
            try {
                Thread.sleep(500);
                Logger.i("dispatch: %s", imageRequest);
            } catch (InterruptedException ignored) {

            } finally {
                latch.countDown();
            }
        }

        @Override
        public boolean cancel(@NonNull ImageRequest imageRequest, boolean interruptRunning) {
            return false;
        }

        @Override
        public void cancelAll(boolean interruptRunning) {

        }
    }
}
