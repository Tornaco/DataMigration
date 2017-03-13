package org.newstand.datamigration.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.WorkerThread;

import com.orhanobut.logger.Logger;

import org.newstand.datamigration.common.ActionListener;
import org.newstand.datamigration.data.DataCategory;
import org.newstand.datamigration.data.DataRecord;
import org.newstand.datamigration.thread.SharedExecutor;

import java.util.List;

/**
 * Created by Nick@NewStand.org on 2017/3/10 9:34
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public final class DataSelectionKeeperServiceProxy extends ServiceProxy {

    private DataSelectionKeeperService.BinderStub mService;

    private DataSelectionKeeperServiceProxy(Context context) {
        super(context, new Intent(context, DataSelectionKeeperService.class));
    }

    @Override
    public void onConnected(IBinder binder) {
        mService = (DataSelectionKeeperService.BinderStub) binder;
    }

    @WorkerThread
    private List<DataRecord> getSelectionByCategory(final DataCategory category) {
        Logger.d("getSelectionByCategoryAsync");
        final ResultHolder<List<DataRecord>> holder = new ResultHolder<>();
        setTask(new ProxyTask() {
            @Override
            public void run() throws RemoteException {
                holder.res = mService.getSelectionByCategory(category);
            }
        });
        waitForCompletion();
        return holder.res;
    }

    private class ResultHolder<T> {
        T res;
    }

    @WorkerThread
    public static List<DataRecord> getSelectionByCategory(Context context, DataCategory category) {
        return new DataSelectionKeeperServiceProxy(context).getSelectionByCategory(category);
    }

    public static void getSelectionByCategoryAsync(final Context context, final DataCategory category,
                                                   final ActionListener<List<DataRecord>> actionListener) {
        Logger.d("getSelectionByCategoryAsync");
        Runnable r = new Runnable() {
            @Override
            public void run() {
                actionListener.onAction(getSelectionByCategory(context, category));
            }
        };
        SharedExecutor.execute(r);
    }

    public static void start(Context context) {
        context.startService(new Intent(context, DataSelectionKeeperService.class));
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, DataSelectionKeeperService.class));
    }
}
