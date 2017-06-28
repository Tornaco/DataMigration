package org.newstand.datamigration.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import org.newstand.datamigration.common.ActionListener;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick on 2017/6/27 22:07
 */

public class DataCompressServiceProxy extends ServiceProxy implements DataCompresser {

    @Getter
    @Setter
    private DataCompresser compresser;

    public DataCompressServiceProxy(Context context) {
        super(context, new Intent(context, DataCompressService.class));
        context.startService(new Intent(context, DataCompressService.class));
    }

    @Override
    public void compressAsync(final String src, final String dest,
                              final ActionListener<Boolean> listener) {
        setTask(new ProxyTask() {
            @Override
            public void run() throws RemoteException {
                getCompresser().compressAsync(src, dest, listener);
            }
        });
    }

    @Override
    public void onConnected(IBinder binder) {
        setCompresser((DataCompresser) binder);
    }
}
