package org.newstand.datamigration.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.provider.ThemeColor;
import org.newstand.datamigration.ui.activity.TransitionSafeActivity;
import org.newstand.datamigration.ui.activity.TransportFailureStatsViewerActivity;
import org.newstand.datamigration.ui.activity.TransportStatsViewerActivity;
import org.newstand.datamigration.ui.tiles.ThemedCategory;
import org.newstand.datamigration.ui.widget.TypeFaceHelper;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.backup.TransportType;
import org.newstand.logger.Logger;

import java.util.List;

import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;
import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.QuickTileView;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/29 13:25
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

@Getter
public abstract class DataTransportUIFragment extends DashboardFragment {

    @Getter
    private ThemeColor themeColor;

    @Getter
    @Setter
    private Session session;

    abstract TransportType getTransportType();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        themeColor = SettingsProvider.getThemeColor();
    }

    protected void transitionTo(Intent intent) {
        TransitionSafeActivity transitionSafeActivity = (TransitionSafeActivity) getActivity();
        transitionSafeActivity.transitionTo(intent);
    }

    private static final long DURATION = 500;

    @SuppressWarnings("unchecked")
    protected <T extends View> T findView(View root, @IdRes int idRes) {
        return (T) root.findViewById(idRes);
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    protected <T extends View> T findView(@IdRes int idRes) {
        return (T) getView().findViewById(idRes);
    }

    public boolean isAlive() {
        return !isDetached() && isAdded() && getActivity() != null;
    }

    public String getStringSafety(@StringRes int idRes) {
        if (!isAlive()) return null;
        return getString(idRes);
    }

    public final String getStringSafety(@StringRes int resId, Object... formatArgs) {
        if (!isAlive()) return null;
        return getString(resId, formatArgs);
    }

    @Getter
    private Handler handler = new Handler(Looper.getMainLooper());

    protected static final int STATE_UNINITIALIZED = -0X100;

    @Getter
    private int state = STATE_UNINITIALIZED;

    public void enterState(final int state) {
        enterState(state, null);
    }

    public void enterState(final int state, final Object obj) {
        this.state = state;
        post(new Runnable() {
            @Override
            public void run() {
                handleState(state, obj);
            }
        });
    }

    protected void post(Runnable r) {
        handler.post(r);
    }

    @UiThread
    abstract void handleState(int state, Object obj);

    private TextView progressTextView;
    private TextView recordTitleTextView, recordEventTextView;
    private ProgressBar progressBar;
    private FloatingActionButton fab;

    @Override
    protected int getLayoutId() {
        return R.layout.data_transporter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        assert root != null;
        progressTextView = (TextView) root.findViewById(R.id.progress_text);
        progressTextView.setKeepScreenOn(true);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressTextView.setTypeface(TypeFaceHelper.get(getContext(), "GOTHICB.ttf"));
            }
        });

        recordTitleTextView = (TextView) root.findViewById(android.R.id.title);
        recordEventTextView = (TextView) root.findViewById(android.R.id.text1);

        progressBar = (ProgressBar) root.findViewById(R.id.progress_bar);

        fab = (FloatingActionButton) root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFabClick();
            }
        });
        fab.setEnabled(false);

        return root;
    }

    @Getter
    private SuccessTile successTile;
    @Getter
    private FailureTile failureTile;

    @Override
    protected void onCreateDashCategories(List<Category> categories) {
        super.onCreateDashCategories(categories);

        Category stats = new ThemedCategory();
        stats.titleRes = R.string.title_transport_stats;

        successTile = new SuccessTile(getContext());
        stats.addTile(successTile);
        failureTile = new FailureTile(getContext());
        stats.addTile(failureTile);

        categories.add(stats);
    }

    protected void onNewSuccess() {
        getSuccessTile().onNewSuccess();
    }

    protected void onNewFailure() {
        getFailureTile().onNewFailure();
    }

    @SuppressLint("DefaultLocale")
    protected void setProgress(int progress) {
        getProgressTextView().setText(String.format("%d%%", progress));
    }

    protected void setRecordTitle(String title) {
        getRecordTitleTextView().setText(title);
    }

    protected void setRecordEvent(String event) {
        getRecordEventTextView().setText(event);
    }

    protected void setRecordProgress(int progress) {
        getProgressBar().setProgress(progress);
    }

    protected void onFabClick() {
    }

    private class SuccessTile extends QuickTile {

        private int successCount = 0;

        public void onNewSuccess() {
            this.successCount = successCount + 1;
            getTileView().getSummaryTextView().setText(String.valueOf(successCount));
        }

        public int getSuccessCount() {
            return successCount;
        }

        public SuccessTile(@NonNull final Context context) {
            super(context, null);

            this.titleRes = R.string.title_transport_stats_success;
            this.iconRes = R.drawable.ic_ok;
            this.summary = String.valueOf(successCount);
            this.tileView = new QuickTileView(context, this) {
                @Override
                public void onClick(View v) {
                    super.onClick(v);
                    if (getSuccessCount() == 0) return;
                    if (getSession() == null) {
                        Logger.e("Session has not been ready");
                        return;
                    }
                    Intent intent = new Intent(context, TransportStatsViewerActivity.class);
                    intent.putExtra(IntentEvents.KEY_SOURCE, getSession());
                    intent.putExtra(IntentEvents.KEY_TRANSPORT_TYPE, getTransportType().name());
                    startActivity(intent);
                }
            };
        }
    }

    private class FailureTile extends QuickTile {

        private int failCount = 0;

        public void onNewFailure() {
            this.failCount = failCount + 1;
            getTileView().getSummaryTextView().setText(String.valueOf(failCount));
        }

        public int getFailCount() {
            return failCount;
        }

        public FailureTile(@NonNull final Context context) {
            super(context, null);

            this.titleRes = R.string.title_transport_stats_fail;
            this.iconRes = R.drawable.ic_fail;
            this.summary = String.valueOf(failCount);
            this.tileView = new QuickTileView(context, this) {
                @Override
                public void onClick(View v) {
                    super.onClick(v);
                    if (getFailCount() == 0) return;
                    if (getSession() == null) {
                        Logger.e("Session has not been ready");
                        return;
                    }
                    Intent intent = new Intent(context, TransportFailureStatsViewerActivity.class);
                    intent.putExtra(IntentEvents.KEY_SOURCE, getSession());
                    intent.putExtra(IntentEvents.KEY_TRANSPORT_TYPE, getTransportType().name());
                    startActivity(intent);
                }
            };
        }
    }
}
