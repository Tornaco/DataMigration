package org.newstand.datamigration.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class MediaScannerClient implements MediaScannerConnection.MediaScannerConnectionClient {

    private ArrayList<String> mPaths = new ArrayList<>();

    private MediaScannerConnection mScannerConnection;

    private boolean mConnected;

    private Runnable mRunnable;

    public static void scanSync(Context context, String path) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        new MediaScannerClient(context)
                .scanPath(path, new Runnable() {
                    @Override
                    public void run() {
                        latch.countDown();
                    }
                });
        latch.await();
    }

    public MediaScannerClient(Context context) {
        mScannerConnection = new MediaScannerConnection(context, this);
    }

    public void scanPath(String path, Runnable doOnComplete) {
        mRunnable = doOnComplete;
        if (mConnected) {
            mScannerConnection.scanFile(path, null);
        } else {
            mPaths.add(path);
            mScannerConnection.connect();
        }
    }

    public void scanPath(String path) {
        scanPath(path, null);
    }

    @Override
    public void onMediaScannerConnected() {
        mConnected = true;
        if (!mPaths.isEmpty()) {
            for (String path : mPaths) {
                mScannerConnection.scanFile(path, null);
            }
            mPaths.clear();
        }
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        if (mRunnable != null) mRunnable.run();
    }
}