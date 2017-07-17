package org.newstand.datamigration.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.ActionListener2Adapter;
import org.newstand.datamigration.common.Producer;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.secure.VersionCheckResult;
import org.newstand.datamigration.secure.VersionInfo;
import org.newstand.datamigration.secure.VersionRetriever;
import org.newstand.datamigration.ui.activity.HelpActivity;
import org.newstand.datamigration.ui.activity.TransitionSafeActivity;
import org.newstand.datamigration.ui.tiles.AdsTile;
import org.newstand.datamigration.ui.tiles.ReceiveTile;
import org.newstand.datamigration.ui.tiles.ReceivedViewerTile;
import org.newstand.datamigration.ui.tiles.SendTile;
import org.newstand.datamigration.ui.tiles.ShareTile;
import org.newstand.datamigration.ui.tiles.ThemedCategory;
import org.newstand.datamigration.ui.widget.VersionInfoDialog;
import org.newstand.datamigration.utils.EmojiUtils;

import java.util.List;

import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;
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

        findView(rootView, R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitionTo(new Intent(getContext(), HelpActivity.class));
            }
        });

        if (SettingsProvider.shouldCheckForUpdateNow()) {
            checkForUpdate();
        }
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

    protected void transitionTo(Intent intent) {
        TransitionSafeActivity transitionSafeActivity = (TransitionSafeActivity) getActivity();
        transitionSafeActivity.transitionTo(intent);
    }

    @Override
    public Integer produce() {
        return R.string.title_transport_sender_receiver;
    }
}