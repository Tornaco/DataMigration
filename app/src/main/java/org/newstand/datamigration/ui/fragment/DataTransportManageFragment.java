package org.newstand.datamigration.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.text.TextUtils;

import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.ui.widget.ViewAnimateUtils;
import org.newstand.datamigration.worker.transport.RecordEvent;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.TransportListenerMainThreadAdapter;

import java.io.File;

import cn.iwgang.simplifyspan.SimplifySpanBuild;
import dev.nick.eventbus.Event;
import dev.nick.eventbus.EventBus;

/**
 * Created by Nick@NewStand.org on 2017/3/10 9:30
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public abstract class DataTransportManageFragment extends DataTransportLogicFragment {

    protected TransportListenerMainThreadAdapter onCreateTransportListener() {
        return new TransportListenerMainThreadAdapter() {
            @Override
            public void onStartMainThread() {
                super.onStartMainThread();
            }

            @Override
            public void onCompleteMainThread() {
                super.onCompleteMainThread();
            }

            @Override
            public void onRecordFailMainThread(DataRecord record, Throwable err) {
                super.onRecordFailMainThread(record, err);
            }

            @Override
            public void onRecordSuccessMainThread(DataRecord record) {
                super.onRecordSuccessMainThread(record);
            }

            @Override
            public void onRecordStartMainThread(DataRecord record) {
                super.onRecordStartMainThread(record);
                showCurrentRecordInUI(record);
            }

            @Override
            public void onRecordProgressUpdateMainThread(DataRecord record, RecordEvent recordEvent, float progress) {
                super.onRecordProgressUpdateMainThread(record, recordEvent, progress);
                showRecordProgressInUI(record, recordEvent, progress);
            }

            @Override
            public void onProgressUpdateMainThread(float progress) {
                super.onProgressUpdateMainThread(progress);
                updateProgressWheel(progress);
            }
        };
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        readyToGo();
    }

    protected void readyToGo() {
        enterState(STATE_TRANSPORT_START);
        setSession(onCreateSession());
    }

    protected abstract Session onCreateSession();

    @UiThread
    @Override
    void handleState(int state, Object obj) {
        switch (state) {
            case STATE_TRANSPORT_START:
                onTransportStart();
                break;
            case STATE_TRANSPORT_PROGRESS_UPDATE:
                updateProgressWheel((Float) obj);
                break;
            case STATE_TRANSPORT_END:
                broadcastCompleteEvent();
                onComplete();
                break;
        }
    }

    @StringRes
    abstract int getStartTitle();

    @StringRes
    abstract int getCompleteTitle();

    protected boolean isCancelable() {
        return true;
    }

    private void onTransportStart() {
        updateConsoleTitleViewOnStart();
        initProgressOnStart();

        // Start log tracker.
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                startLoggerReDirection();
            }
        });
    }

    protected void updateConsoleTitleViewOnStart() {
        getConsoleTitleView().setText(getStartTitle());
    }

    protected void initProgressOnStart() {
        getProgressBar().setText(String.valueOf(0));
        getProgressBar().setProgress(0);
    }

    protected void showCurrentRecordInUI(DataRecord record) {
        getConsoleTitleView().setText(record.getDisplayName());
    }

    protected void showRecordProgressInUI(DataRecord record,
                                          RecordEvent recordEvent, float pieceProgress) {
        getConsoleSummaryView().setText(getStringSafety(recordEvent.getDescription()));
        getBottomProgressBar().setProgress((int) pieceProgress);
    }

    protected void updateProgressWheel(float progress) {
        getProgressBar().setText(String.valueOf((int) (progress)));
        getProgressBar().setProgress((int) (progress / 100 * 360));
    }

    private void onComplete() {

        // Stop log tracker.
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                stopLoggerRedirection();
            }
        });

        ViewAnimateUtils.alphaHide(getConsoleCardView(), new Runnable() {
            @Override
            public void run() {
                getProgressBar().setText("100");
                getProgressBar().setProgress(360);

                getConsoleTitleView().setText(getCompleteTitle());

                updateCompleteSummary();

                ViewAnimateUtils.alphaShow(getConsoleCardView());
            }
        });
    }

    protected void updateCompleteSummary() {
        if (!isAlive()) return;
        SimplifySpanBuild summary = onCreateCompleteSummary();
        getConsoleSummaryView().setText(summary.build());
    }

    abstract SimplifySpanBuild onCreateCompleteSummary();

    protected void broadcastCompleteEvent() {
        EventBus.from(getContext()).publish(Event.builder()
                .eventType(IntentEvents.EVENT_TRANSPORT_COMPLETE)
                .obj(getSession()).build());
    }

    protected boolean validateInput(String currentName, CharSequence in) {
        return !TextUtils.isEmpty(in) && (!currentName.equals(in.toString()))
                && !in.toString().contains(" ") // FIXME Tell user.
                && !in.toString().contains("Tmp_")
                && !in.toString().contains(File.separator);
    }
}