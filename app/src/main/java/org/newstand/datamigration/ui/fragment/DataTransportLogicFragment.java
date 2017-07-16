package org.newstand.datamigration.ui.fragment;

import android.content.Context;
import android.support.annotation.WorkerThread;

import com.google.common.io.Files;

import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/29 13:27
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class DataTransportLogicFragment extends DataTransportUIFragment {

    protected static final int STATE_ATTACHED = 0x1;
    protected static final int STATE_DETACHED = 0x2;

    protected static final int STATE_TRANSPORT_START = 0x3;
    protected static final int STATE_TRANSPORT_END = 0x4;
    protected static final int STATE_TRANSPORT_PROGRESS_UPDATE = 0x5;

    @Getter
    private String logFileName = SettingsProvider.getLogDir() + File.separator + UUID.randomUUID().toString();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        enterState(STATE_ATTACHED);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        enterState(STATE_DETACHED);
    }

    @WorkerThread
    protected void startLoggerReDirection() {
        try {
            Files.createParentDirs(new File(getLogFileName()));
            Logger.startRedirection(new PrintStream(new File(getLogFileName())));
        } catch (IOException e) {
            Logger.e(e, "Fail start logger redirection");
        }
    }

    @WorkerThread
    protected void stopLoggerRedirection() {
        Logger.stopRedirection();
    }
}
