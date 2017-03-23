package org.newstand.datamigration.net;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.common.io.Files;
import com.orhanobut.logger.Logger;

import org.newstand.datamigration.common.ContextWireable;
import org.newstand.datamigration.net.protocol.ACK;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/22 11:08
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class AbsSender<T> implements Sender<T>, ContextWireable {

    @Getter
    @Setter
    private Context context;

    @Override
    public void wire(@NonNull Context context) {
        setContext(context);
    }

    protected int waitForAck(InputStream is) throws IOException {
        byte[] ack = ACK.allocate();

        int ret = is.read(ack);

        if (ret == -1) {
            return ERR_READ_ACK;
        }

        if (!ACK.isOk(ack)) {
            return ERR_BAD_ACK;
        }

        Logger.i("ACK in");

        return OK;
    }

    protected int writeFile(String path, OutputStream os) throws IOException {
        long size = Files.asByteSource(new File(path)).size();

        long written = Files.asByteSource(new File(path)).copyTo(os);

        if (size != written) {
            return ERR_WRITE_FAIL_IN_OUT_SIZE_MISMATCH;
        }
        return OK;
    }
}
