package org.newstand.datamigration.loader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.common.io.Files;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.repo.BKSessionRepoService;
import org.newstand.datamigration.repo.ReceivedSessionRepoService;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.SessionComparator;
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
                    // Load delegate database.
                    List<Session> all = BKSessionRepoService.get().findAll(context);
                    Collections.consumeRemaining(all, new Consumer<Session>() {
                        @Override
                        public void accept(@NonNull Session session) {
                            if (!validate(LoaderSource.builder().parent(LoaderSource.Parent.Backup).build(), session)) {
                                Logger.w("Ignored bad session %s", session);
                                return;
                            }
                            res.add(Session.from(session));
                        }
                    });

                    String root = SettingsProvider.getBackupRootDir();
                    // Check missing.
                    Iterable<File> iterable = Files.fileTreeTraverser().children(new File(root));

                    if (iterable != null) {
                        Collections.consumeRemaining(iterable, new Consumer<File>() {
                            @Override
                            public void accept(@NonNull File file) {
                                if (file.isDirectory()) {
                                    String name = Files.getNameWithoutExtension(file.getPath());
                                    if (!TextUtils.isEmpty(name) && !isSameFileExistInSessions(res, name)) {
                                        Logger.d("Found maybe missing backup dir: %s, importing...", file);
                                        if (!org.newstand.datamigration.utils.Files.isEmptyDir(file)) {
                                            // Now import it into db.
                                            Session session = Session.from(name, file.lastModified());
                                            Logger.d("Created new session: %s for importing", session);
                                            BKSessionRepoService.get().insert(context, session);
                                            res.add(session);
                                        }
                                    }
                                }
                            }
                        });
                    }
                    java.util.Collections.sort(res, new SessionComparator());
                    loaderListener.onComplete(res);
                } catch (Throwable throwable) {
                    loaderListener.onErr(throwable);
                }
            }
        };

        SharedExecutor.execute(r);
    }

    private static boolean isSameFileExistInSessions(List<Session> sessions, String name) {
        for (Session s : sessions) {
            if (name.equals(s.getName())) {
                return true;
            }
        }
        return false;
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

                    String root = SettingsProvider.getReceivedRootDir();
                    // Check missing.
                    Iterable<File> iterable = Files.fileTreeTraverser().children(new File(root));

                    if (iterable != null) {
                        Collections.consumeRemaining(iterable, new Consumer<File>() {
                            @Override
                            public void accept(@NonNull File file) {
                                if (file.isDirectory()) {
                                    String name = Files.getNameWithoutExtension(file.getPath());
                                    if (!TextUtils.isEmpty(name) && !isSameFileExistInSessions(res, name)) {
                                        Logger.d("Found maybe missing received dir: %s, importing...", file);
                                        if (!org.newstand.datamigration.utils.Files.isEmptyDir(file)) {
                                            // Now import it into db.
                                            Session session = Session.from(name, file.lastModified());
                                            Logger.d("Created new session: %s for received", session);
                                            ReceivedSessionRepoService.get().insert(context, session);
                                            res.add(session);
                                        }
                                    }
                                }
                            }
                        });
                    }

                    java.util.Collections.sort(res, new SessionComparator());
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
