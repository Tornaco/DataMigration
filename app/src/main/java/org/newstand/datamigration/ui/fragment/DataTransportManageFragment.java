package org.newstand.datamigration.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.View;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.AbortSignal;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.ui.widget.ViewAnimateUtils;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.Stats;
import org.newstand.datamigration.worker.backup.session.Session;

import cn.iwgang.simplifyspan.SimplifySpanBuild;
import cn.iwgang.simplifyspan.unit.SpecialTextUnit;
import dev.nick.eventbus.Event;
import dev.nick.eventbus.EventBus;

/**
 * Created by Nick@NewStand.org on 2017/3/10 9:30
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public abstract class DataTransportManageFragment extends DataTransportLogicFragment {

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

    @Override
    void handleState(int state) {
        switch (state) {
            case STATE_TRANSPORT_START:
                onTransportStart();
                break;
            case STATE_TRANSPORT_PROGRESS_UPDATE:
                onProgressUpdate();
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
        getConsoleTitleView().setText(getStartTitle());
        getConsoleDoneButton().setText(android.R.string.cancel);
        getConsoleDoneButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.consumeRemaining(getAbortSignals(), new Consumer<AbortSignal>() {
                    @Override
                    public void consume(@NonNull AbortSignal abortSignal) {
                        abortSignal.abort();
                    }
                });
            }
        });
    }

    protected void onProgressUpdate() {
        Stats stats = getStats();
        float total = (float) stats.getTotal();
        float ok = (float) stats.getTotal() - (float) stats.getLeft();
        float progress = (ok / total);
        getProgressBar().setText(String.valueOf((int) (progress * 100)));
        getProgressBar().setProgress((int) (progress * 360));
    }

    private void onComplete() {

        ViewAnimateUtils.alphaHide(getConsoleCardView(), new Runnable() {
            @Override
            public void run() {
                getProgressBar().setText("100");
                getProgressBar().setProgress(360);

                getConsoleTitleView().setText(getCompleteTitle());
                getConsoleDoneButton().setText(R.string.action_done);
                getConsoleDoneButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onDoneButtonClick();
                    }
                });

                updateCompleteSummary();

                ViewAnimateUtils.alphaShow(getConsoleCardView());
            }
        });
    }

    abstract void onDoneButtonClick();

    protected void updateCompleteSummary() {
        if (!isAlive()) return;
        SimplifySpanBuild summary = onCreateCompleteSummary();
        getConsoleSummaryView().setText(summary.build());
    }

    abstract SimplifySpanBuild onCreateCompleteSummary();

    protected SimplifySpanBuild buildTransportReport(Stats stats) {
        SimplifySpanBuild report = new SimplifySpanBuild();
        if (!isAlive()) return report;
        report.append(getString(R.string.title_transport_report_total, String.valueOf(stats.getTotal())));
        report.append("\n");
        report.append(new SpecialTextUnit(getString(R.string.title_transport_report_success,
                String.valueOf(stats.getSuccess())), ContextCompat.getColor(getContext(), R.color.green_dark)));
        report.append("\n");
        report.append(new SpecialTextUnit(getString(R.string.title_transport_report_fail,
                String.valueOf(stats.getFail())), ContextCompat.getColor(getContext(), R.color.red_dark)));
        return report;
    }


    protected void broadcastCompleteEvent() {
        EventBus.from(getContext()).publish(Event.builder()
                .eventType(IntentEvents.EVENT_TRANSPORT_COMPLETE)
                .obj(getSession()).build());
    }

}