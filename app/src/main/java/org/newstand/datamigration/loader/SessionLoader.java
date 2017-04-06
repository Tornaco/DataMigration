package org.newstand.datamigration.loader;

import android.support.annotation.NonNull;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.repo.BKSessionRepoService;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.backup.session.Session;
import org.newstand.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Nick@NewStand.org on 2017/3/9 15:46
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class SessionLoader {

    public static void loadAsync(final LoaderListener<Session> loaderListener) {
        loadAsync(LoaderSource.builder().parent(LoaderSource.Parent.Backup).build(), loaderListener);
    }

    public static void loadAsync(LoaderSource source, final LoaderListener<Session> loaderListener) {
        switch (source.getParent()) {
            case Backup:
                loadFromBackupAsync(loaderListener);
                break;
            case Received:
                loadFromReceivedAsync(loaderListener);
                break;
            default:
                throw new IllegalArgumentException("Bad source:" + source);
        }
    }

    private static void loadFromBackupAsync(final LoaderListener<Session> loaderListener) {

        final Collection<Session> res = new ArrayList<>();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                loaderListener.onStart();
                try {
                    List<Session> all = BKSessionRepoService.get().findAll();
                    Collections.consumeRemaining(all, new Consumer<Session>() {
                        @Override
                        public void consume(@NonNull Session session) {
                            if (!validate(session)) {
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
                    loaderListener.onComplete(res);
                } catch (Throwable throwable) {
                    loaderListener.onErr(throwable);
                }
            }
        };

        SharedExecutor.execute(r);
    }

    private static void loadFromReceivedAsync(final LoaderListener<Session> loaderListener) {

        final Collection<Session> res = new ArrayList<>();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                loaderListener.onStart();
                try {
                    List<Session> all = BKSessionRepoService.get().findAll();
                    Collections.consumeRemaining(all, new Consumer<Session>() {
                        @Override
                        public void consume(@NonNull Session session) {
                            if (!validate(session)) {
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
                    loaderListener.onComplete(res);
                } catch (Throwable throwable) {
                    loaderListener.onErr(throwable);
                }
            }
        };

        SharedExecutor.execute(r);
    }

    private static boolean validate(Session session) {
        File file = new File(SettingsProvider.getBackupSessionDir(session));
        return file.exists();
    }
}
