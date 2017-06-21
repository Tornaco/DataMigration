package org.newstand.datamigration.ui.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.ActionListener2Adapter;
import org.newstand.datamigration.common.Producer;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.repo.BKSessionRepoService;
import org.newstand.datamigration.secure.VersionCheckResult;
import org.newstand.datamigration.secure.VersionInfo;
import org.newstand.datamigration.secure.VersionRetriever;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.ui.activity.TransitionSafeActivity;
import org.newstand.datamigration.ui.tiles.AdsTile;
import org.newstand.datamigration.ui.tiles.BackupTile;
import org.newstand.datamigration.ui.tiles.CategoriesToLoadTile;
import org.newstand.datamigration.ui.tiles.RestoreTile;
import org.newstand.datamigration.ui.tiles.RulesTile;
import org.newstand.datamigration.ui.tiles.SchedulerTile;
import org.newstand.datamigration.ui.tiles.ThemedCategory;
import org.newstand.datamigration.ui.widget.ErrDialog;
import org.newstand.datamigration.ui.widget.IntroDialog;
import org.newstand.datamigration.ui.widget.VersionInfoDialog;
import org.newstand.datamigration.utils.EmojiUtils;
import org.newstand.datamigration.worker.transport.Session;

import java.util.Date;
import java.util.List;

import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;
import io.reactivex.functions.Consumer;
import lombok.Getter;
import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter;

/**
 * Created by Nick@NewStand.org on 2017/4/21 9:42
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class BackupRestoreNavigatorFragment extends DashboardFragment implements Producer<Integer> {

    public static BackupRestoreNavigatorFragment create() {
        return new BackupRestoreNavigatorFragment();
    }

    @Getter
    private View rootView;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_home_backup_restore;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        setupView();
        return rootView;
    }

    @Override
    protected void onCreateDashCategories(List<Category> categories) {
        super.onCreateDashCategories(categories);

        Category actions = new ThemedCategory();
        actions.titleRes = R.string.title_card_actions;
        actions.addTile(new BackupTile(getActivity()));
        actions.addTile(new RestoreTile(getActivity()));

        Category actions2 = new ThemedCategory();
        actions.titleRes = R.string.title_card_actions2;
        actions2.addTile(new CategoriesToLoadTile(getActivity()));
        actions2.addTile(new SchedulerTile(getActivity()));
        actions2.addTile(new RulesTile(getActivity()));

        categories.add(actions);
        categories.add(actions2);

        if (SettingsProvider.isShowAdEnabled()) {
            Category adc = new ThemedCategory();
            adc.titleRes = R.string.title_card_ad;
            adc.addTile(new AdsTile(getActivity()));
            categories.add(adc);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T findView(@IdRes int idRes) {
        return (T) getRootView().findViewById(idRes);
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T findView(View root, @IdRes int idRes) {
        return (T) root.findViewById(idRes);
    }

    protected void setupView() {

        findView(rootView, R.id.card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Toast.makeText(getActivity(), EmojiUtils.getEmojiByUnicode(0x1F60F), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (SettingsProvider.shouldCheckForUpdateNow()) {
            checkForUpdate();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Show intro dialog
        IntroDialog.attach(getContext(), new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                getActivity().finish();
            }
        }, new Runnable() {
            @Override
            public void run() {
                // Ask for perms
                requestPerms();
            }
        });
    }

    private void requestPerms() {
        RxPermissions rxPermissions = new RxPermissions(getActivity());
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) {
                            onPermissionGrant();
                        } else {
                            onPermissionNotGrant();
                        }
                    }
                });
    }


    private void checkForUpdate() {
        VersionRetriever.hasLaterVersionAsync(getContext(), new ActionListener2Adapter<VersionCheckResult, Throwable>() {
            @Override
            public void onComplete(VersionCheckResult versionCheckResult) {
                super.onComplete(versionCheckResult);
                if (versionCheckResult.isHasLater()) {
                    showUpdateSnake(versionCheckResult.getVersionInfo());
                    SettingsProvider.setLastUpdateCheckTime(System.currentTimeMillis());
                }
            }
        });
    }

    private void showUpdateSnake(final VersionInfo info) {
        Snackbar.make(findView(R.id.fab),
                getString(R.string.title_new_update_available, info.getVersionName()),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_look_up, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRequestLookup(info);
                    }
                }).show();
    }

    private void onRequestLookup(VersionInfo info) {
        VersionInfoDialog.attach(getContext(), info);
    }

    private void onPermissionGrant() {
        queryShowHistory();
    }

    private void onPermissionNotGrant() {
        ErrDialog.attach(getContext(), new IllegalStateException("Permission denied"), new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                getActivity().finish();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void queryShowHistory() {
        final TextView tv1 = findView(findView(R.id.card), android.R.id.text2);
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Session last = BKSessionRepoService.get().findFirst(getContext());
                final String intro;
                if (last == null) {
                    intro = getString(R.string.title_backup_history_noop);
                } else {
                    intro = getString(R.string.title_backup_history,
                            FuzzyDateTimeFormatter.getTimeAgo(getContext(),
                                    new Date(last.getDate())));
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv1.setText(intro);
                    }
                });
            }
        });
    }

    protected void transitionTo(Intent intent) {
        TransitionSafeActivity transitionSafeActivity = (TransitionSafeActivity) getActivity();
        transitionSafeActivity.transitionTo(intent);
    }

    @Override
    public Integer produce() {
        return R.string.title_backup_restore;
    }
}
