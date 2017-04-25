package org.newstand.datamigration.loader;

import android.content.Context;
import android.support.annotation.NonNull;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.repo.BKSessionRepoService;
import org.newstand.datamigration.repo.ReceivedSessionRepoService;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick@NewStand.org on 2017/3/9 15:46
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class SessionLoader {

    public static void loadAsync(Context context, final LoaderListener<Session> loaderListener) {
        loadAsync(context, LoaderSource.builder().parent(LoaderSource.Parent.Backup).build(), loaderListener);
    }

    public static void loadAsync(Context context, LoaderSource source, final LoaderListener<Session> loaderListener) {
        switch (source.getParent()) {
            case Backup:
                loadFromBackupAsync(context, loaderListener);
                break;
            case Received:
                loadFromReceivedAsync(context, loaderListener);
                break;
            default:
                throw new IllegalArgumentException("Bad source:" + source);
        }
    }

    private static void loadFromBackupAsync(final Context context, final LoaderListener<Session> loaderListener) {

        final List<Session> res = new ArrayList<>();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                loaderListener.onStart();
                try {
                    List<Session> all = BKSessionRepoService.get().findAll(context);
                    Collections.consumeRemaining(all, new Consumer<Session>() {
                        @Override
                        public void accept(@NonNull Session session) {
                            if (!validate(LoaderSource.builder().parent(LoaderSource.Parent.Backup).build(), session)) {
                                Logger.w("Ignored bad session %s", session);
                                return;
                            }
                            if (!session.isTmp()) {
                                res.add(Session.from(session));
                            } else {
                                Logger.w("Ignored tmp session %s", session);
                            }
                        }
                    });
                    java.util.Collections.reverse(res);
                    loaderListener.onComplete(res);
                } catch (Throwable throwable) {
                    loaderListener.onErr(throwable);
                }
            }
        };

        SharedExecutor.execute(r);
    }

    private static void loadFromReceivedAsync(final Context context, final LoaderListener<Session> loaderListener) {

        final List<Session> res = new ArrayList<>();

        final Runnable r = new Runnable() {
            @Override
            public void run() {
                loaderListener.onStart();
                try {
                    List<Session> all = ReceivedSessionRepoService.get().findAll(context);
                    Collections.consumeRemaining(all, new Consumer<Session>() {
                        @Override
                        public void accept(@NonNull Session session) {
                            if (!validate(LoaderSource.builder().parent(LoaderSource.Parent.Received).build(), session)) {
                                Logger.w("Ignored bad session %s", session);
                                return;
                            }
                            res.add(Session.from(session));
                        }
                    });
                    java.util.Collections.reverse(res);
                    loaderListener.onComplete(res);
                } catch (Throwable throwable) {
                    loaderListener.onErr(throwable);
                }
            }
        };

        SharedExecutor.execute(r);
    }

    private static boolean validate(LoaderSource source, Session session) {
        String path = source.getParent() == LoaderSource.Parent.Backup
                ? SettingsProvider.getBackupSessionDir(session)
                : SettingsProvider.getRecSessionDir(session);
        File file = new File(path);
        return file.exists();
    }
}
