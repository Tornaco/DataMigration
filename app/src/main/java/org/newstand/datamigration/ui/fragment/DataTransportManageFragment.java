package org.newstand.datamigration.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.AbortSignal;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.data.event.UserAction;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.service.UserActionServiceProxy;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.ui.widget.ViewAnimateUtils;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.Stats;
import org.newstand.logger.Logger;

import java.util.List;

import cn.iwgang.simplifyspan.SimplifySpanBuild;
import cn.iwgang.simplifyspan.other.OnClickableSpanListener;
import cn.iwgang.simplifyspan.unit.SpecialClickableUnit;
import cn.iwgang.simplifyspan.unit.SpecialTextUnit;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;
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

    @UiThread
    @Override
    void handleState(int state, Object obj) {
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

    protected boolean isCancelable() {
        return true;
    }

    private void onTransportStart() {
        updateConsoleTitleView();
        updateConsoleDoneButton();
    }

    protected void updateConsoleTitleView() {
        getConsoleTitleView().setText(getStartTitle());
    }

    protected void updateConsoleDoneButton() {
        getConsoleDoneButton().setText(android.R.string.cancel);
        if (isCancelable()) {
            getConsoleDoneButton().setVisibility(View.VISIBLE);
            getConsoleDoneButton()
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Collections.consumeRemaining(getAbortSignals(),
                                    new Consumer<AbortSignal>() {
                                        @Override
                                        public void accept(@NonNull AbortSignal abortSignal) {
                                            Logger.i("Notifying abort signal to %s", abortSignal);
                                            abortSignal.abort();
                                        }
                                    });
                        }
                    });
        } else {
            getConsoleDoneButton().setVisibility(View.INVISIBLE);
        }
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

        getConsoleDoneButton().setVisibility(View.VISIBLE);

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
        buildSummaryIntro();
    }

    private void buildSummaryIntro() {
        new MaterialIntroView.Builder(getActivity())
                .enableDotAnimation(true)
                .enableIcon(true)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.MINIMUM)
                .enableFadeAnimation(true)
                .performClick(false)
                .setInfoText(getString(getSummaryIntro()))
                .setShape(ShapeType.CIRCLE)
                .setTarget(getConsoleSummaryView())
                .setUsageId("intro_transport_management_" + getClass().getName())
                .show();
    }

    private int getSummaryIntro() {
        return R.string.title_transport_intro;
    }

    abstract SimplifySpanBuild onCreateCompleteSummary();

    protected SimplifySpanBuild buildTransportReport(Stats stats) {
        SimplifySpanBuild report = new SimplifySpanBuild();
        if (!isAlive()) return report;
        report.append(getString(R.string.title_transport_report_total, String.valueOf(stats.getTotal())));
        report.append("\n");
        report.append(new SpecialTextUnit(getString(R.string.title_transport_report_success,
                String.valueOf(stats.getSuccess())), ContextCompat.getColor(getContext(), R.color.green_dark))
                .setClickableUnit(new SpecialClickableUnit(getConsoleSummaryView(), new OnClickableSpanListener() {
                    @Override
                    public void onClick(TextView tv, String clickText) {
                        onSuccessTextInSummaryClick();
                    }
                })));
        report.append("\n");
        report.append(new SpecialTextUnit(getString(R.string.title_transport_report_fail,
                String.valueOf(stats.getFail())), ContextCompat.getColor(getContext(), R.color.red_dark))
                .setClickableUnit(new SpecialClickableUnit(getConsoleSummaryView(), new OnClickableSpanListener() {
                    @Override
                    public void onClick(TextView tv, String clickText) {
                        onFailTextInSummaryClick();
                    }
                })));
        return report;
    }

    protected void onFailTextInSummaryClick() {
        Logger.i("onFailTextInSummaryClick");
    }

    protected void onSuccessTextInSummaryClick() {
        Logger.i("onSuccessTextInSummaryClick");
    }

    protected void broadcastCompleteEvent() {
        EventBus.from(getContext()).publish(Event.builder()
                .eventType(IntentEvents.EVENT_TRANSPORT_COMPLETE)
                .obj(getSession()).build());
    }

    protected void publishFailEvent(DataRecord handling, Throwable throwable) {
        Session session = getSession();
        UserActionServiceProxy.publishNewAction(
                getContext(),
                UserAction.builder()
                        .eventTitle("Fail:" + handling.getDisplayName())
                        .date(session.getDate())
                        .fingerPrint(session.getDate())
                        .eventDescription(Logger.getStackTraceString(throwable))
                        .build());
    }

    protected void publishFailEventAsync(final Throwable throwable) {
        publishFailEventAsync(null, throwable);
    }

    protected void publishFailEventAsync(final DataRecord handling, final Throwable throwable) {
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                publishFailEvent(handling, throwable);
            }
        });
    }

    protected List<UserAction> queryFailEvent() {
        Session session = getSession();
        return UserActionServiceProxy.getByFingerPrint(getContext(), session.getDate());
    }

    protected void queryFailEventAsync(final Consumer<List<UserAction>> consumer) {
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                consumer.accept(queryFailEvent());
            }
        });
    }
}