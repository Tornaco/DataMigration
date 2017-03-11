package org.newstand.datamigration.loader;

import android.support.annotation.NonNull;

import com.google.common.io.Files;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.thread.SharedExecutor;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.backup.session.Session;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Nick@NewStand.org on 2017/3/9 15:46
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class SessionLoader {

    public static void loadAsync(final LoaderListener<Session> loaderListener) {
        final Collection<Session> sessionCollection = new ArrayList<>();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                loaderListener.onStart();
                try {
                    File rootDir = new File(SettingsProvider.getBackupRootDir());
                    Collections.consumeRemaining(Files.fileTreeTraverser().children(rootDir),
                            new Consumer<File>() {
                                @Override
                                public void consume(@NonNull File file) {
                                    Session session = Session.from(file.getName());
                                    sessionCollection.add(session);
                                }
                            });
                    loaderListener.onComplete(sessionCollection);
                } catch (Throwable throwable) {
                    loaderListener.onErr(throwable);
                }
            }
        };

        SharedExecutor.execute(r);
    }
}
