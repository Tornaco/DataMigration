package org.newstand.datamigration.net.server;

import android.util.Log;

import com.orhanobut.logger.Logger;

import org.newstand.datamigration.sync.Sleeper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/22 15:21
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@NoArgsConstructor
@ToString(of = {"host", "port"})
public abstract class ServerComponent implements Component, Runnable {

    @Setter(AccessLevel.PROTECTED)
    private OutputStream outputStream;
    @Setter(AccessLevel.PROTECTED)
    private InputStream inputStream;

    @Setter
    private String host;
    @Setter
    private int port;

    @Override
    public String name() {
        return getClass().getSimpleName();
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            if (tryOnce()) return;
            Sleeper.sleepQuietly(3 * 1000);
        }
    }

    private boolean tryOnce() {
        try {
            start();
            return true;
        } catch (IOException e) {
            Logger.d("Failed to start %s", Log.getStackTraceString(e));
            return false;
        }
    }
}
