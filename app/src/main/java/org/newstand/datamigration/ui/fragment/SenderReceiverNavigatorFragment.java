package org.newstand.datamigration.ui.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.ActionListener2Adapter;
import org.newstand.datamigration.common.Producer;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.repo.ReceivedSessionRepoService;
import org.newstand.datamigration.secure.VersionCheckResult;
import org.newstand.datamigration.secure.VersionInfo;
import org.newstand.datamigration.secure.VersionRetriever;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.ui.activity.ReceivedSessionPickerActivity;
import org.newstand.datamigration.ui.activity.TransitionSafeActivity;
import org.newstand.datamigration.ui.activity.WFDDataReceiverActivity;
import org.newstand.datamigration.ui.activity.WFDDataSenderActivity;
import org.newstand.datamigration.ui.tiles.AdsTile;
import org.newstand.datamigration.ui.tiles.ReceiveTile;
import org.newstand.datamigration.ui.tiles.ReceivedViewerTile;
import org.newstand.datamigration.ui.tiles.SendTile;
import org.newstand.datamigration.ui.tiles.ShareTile;
import org.newstand.datamigration.ui.tiles.ThemedCategory;
import org.newstand.datamigration.ui.widget.PermissionMissingDialog;
import org.newstand.datamigration.ui.widget.VersionInfoDialog;
import org.newstand.datamigration.utils.EmojiUtils;
import org.newstand.datamigration.utils.Files;

import java.util.List;

import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;
import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;
import io.reactivex.functions.Consumer;
import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/4/21 9:42
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SenderReceiverNavigatorFragment extends DashboardFragment implements Producer<Integer> {

    public static SenderReceiverNavigatorFragment create() {
        return new SenderReceiverNavigatorFragment();
    }

    @Getter
    private View rootView;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_home_sender_receiver;
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

        actions.addTile(new SendTile(getActivity()));
        actions.addTile(new ReceiveTile(getActivity()));

        Category actions2 = new ThemedCategory();
        actions2.addTile(new ReceivedViewerTile(getActivity()));
        actions2.addTile(new ShareTile(getActivity()));

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
                    Toast.makeText(getActivity(), EmojiUtils.getEmojiByUnicode(0x1F680), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (SettingsProvider.shouldCheckForUpdateNow()) {
            checkForUpdate();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onStart() {
        super.onStart();
        // Ask for perms
        requestPerms();
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
        VersionRetriever.hasLaterVersionAsync(getContext(),
                new ActionListener2Adapter<VersionCheckResult, Throwable>() {
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
    }

    private void buildCardIntros() {
        // Show card intros.
        new MaterialIntroView.Builder(getActivity())
                .enableDotAnimation(true)
                .enableIcon(true)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.MINIMUM)
                .enableFadeAnimation(true)
                .performClick(false)
                .setInfoText(getString(R.string.card_intro))
                .setShape(ShapeType.CIRCLE)
                .setTarget(findView(R.id.card))
                // Always show when in dev mode.
                .setUsageId("intro_card")
                .setListener(new MaterialIntroListener() {
                    @Override
                    public void onUserClicked(String s) {
                        onCardsIntroClick();
                    }
                })
                .show();
    }

    private void onCardsIntroClick() {
    }

    private void onPermissionNotGrant() {
        PermissionMissingDialog.attach(getActivity());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void queryShowHistory() {
        final TextView tv2 = findView(findView(R.id.card), android.R.id.text2);
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int size = ReceivedSessionRepoService.get().size(getContext());
                final String intro;
                if (size == 0) {
                    intro = getString(R.string.title_receive_history_noop);
                } else {
                    intro = getString(R.string.title_receive_history_size, String.valueOf(size));
                }
            }
        });
    }

    private void showCard2Pop(View anchor) {
        PopupMenu popup = new PopupMenu(getActivity(), anchor);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_send:
                        transitionTo(new Intent(getContext(), WFDDataSenderActivity.class));
                        break;
                    case R.id.action_receive:
                        transitionTo(new Intent(getContext(), WFDDataReceiverActivity.class));
                        break;
                    case R.id.action_received_viewer:
                        transitionTo(new Intent(getContext(), ReceivedSessionPickerActivity.class));
                        break;
                    case R.id.action_share:
                        onRequestShare();
                        break;
                }
                return true;
            }
        });
        popup.inflate(R.menu.navigator_card_2);
        popup.show();
    }

    protected void transitionTo(Intent intent) {
        TransitionSafeActivity transitionSafeActivity = (TransitionSafeActivity) getActivity();
        transitionSafeActivity.transitionTo(intent);
    }

    private void onRequestShare() {
        if (!SettingsProvider.isTipsNoticed("onRequestShare"))
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.title_transport_share)
                    .setMessage(R.string.title_transport_share_tips)
                    .setCancelable(true)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            org.newstand.datamigration.utils.Files.shareDateMigrationAsync(getContext());
                        }
                    })
                    .setNeutralButton(R.string.title_never_remind, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SettingsProvider.setTipsNoticed("onRequestShare", true);
                            org.newstand.datamigration.utils.Files.shareDateMigrationAsync(getContext());
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .create()
                    .show();
        else Files.shareDateMigrationAsync(getContext());
    }

    @Override
    public Integer produce() {
        return R.string.title_transport_sender_receiver;
    }
}