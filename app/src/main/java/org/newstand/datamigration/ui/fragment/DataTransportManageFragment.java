package org.newstand.datamigration.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.repo.TransportEventRecordRepoService;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.ui.activity.DataTransportActivity;
import org.newstand.datamigration.worker.transport.RecordEvent;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.TransportListenerMainThreadAdapter;
import org.newstand.logger.Logger;

import dev.nick.eventbus.Event;
import dev.nick.eventbus.EventBus;

/**
 * Created by Nick@NewStand.org on 2017/3/10 9:30
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public abstract class DataTransportManageFragment extends DataTransportLogicFragment implements DataTransportActivity.BackEventListener {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        DataTransportActivity dataTransportActivity = (DataTransportActivity) getActivity();
        dataTransportActivity.setBackEventListener(this);
    }

    @MainThread
    protected TransportListenerMainThreadAdapter onCreateTransportListener() {
        return new TransportListenerMainThreadAdapter() {
            @Override
            public void onStartMainThread() {
                super.onStartMainThread();
            }

            @Override
            public void onCompleteMainThread() {
                super.onCompleteMainThread();
                DataCategory.consumeAllInWorkerThread(new Consumer<DataCategory>() {
                    @Override
                    public void accept(@NonNull DataCategory dataCategory) {
                        Logger.d("Success:%s, %s", dataCategory, TransportEventRecordRepoService.from(getSession())
                                .succeed(getContext(), dataCategory).size());
                        Logger.d("Fail:%s, %s", dataCategory, TransportEventRecordRepoService.from(getSession())
                                .fails(getContext(), dataCategory).size());
                    }
                });
            }

            @Override
            public void onRecordFailMainThread(DataRecord record, Throwable err) {
                super.onRecordFailMainThread(record, err);
                onNewFailure();
            }

            @Override
            public void onRecordSuccessMainThread(DataRecord record) {
                super.onRecordSuccessMainThread(record);
                onNewSuccess();
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
                updateProgress(progress);
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
                updateProgress((Float) obj);
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
        setRecordTitle(getStringSafety(getStartTitle()));
    }

    protected void initProgressOnStart() {
        setProgress(0);
    }

    protected void showCurrentRecordInUI(DataRecord record) {
        setRecordTitle(record.getDisplayName());
    }

    protected void showRecordProgressInUI(DataRecord record,
                                          RecordEvent recordEvent, float pieceProgress) {
        setRecordEvent(getStringSafety(recordEvent.getDescription()));
        setRecordProgress((int) pieceProgress);
    }

    protected void updateProgress(float progress) {
        setProgress((int) progress);
    }

    private void onComplete() {

        // Stop log tracker.
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                stopLoggerRedirection();
            }
        });

        setRecordProgress(100);
        setProgress(100);
        setRecordTitle(getStringSafety(getCompleteTitle()));

        updateCompleteSummary();
    }

    protected void updateCompleteSummary() {
        if (!isAlive()) return;
        String summary = onCreateCompleteSummary();
        setRecordEvent(summary);
    }

    abstract String onCreateCompleteSummary();

    protected void broadcastCompleteEvent() {
        EventBus.from(getContext()).publish(Event.builder()
                .eventType(IntentEvents.EVENT_TRANSPORT_COMPLETE)
                .obj(getSession()).build());
    }

    @Override
    public void onBackEvent() {
        Logger.d("onBackEvent: state=%s", getState());
        if (getState() == STATE_TRANSPORT_END) {
            getActivity().finish();
        }
    }

    @Override
    protected void onFabClick() {
        super.onFabClick();
        getActivity().finish();
    }
}