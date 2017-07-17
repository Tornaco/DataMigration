package org.newstand.datamigration.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.Producer;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.ui.activity.HelpActivity;
import org.newstand.datamigration.ui.activity.TransitionSafeActivity;
import org.newstand.datamigration.ui.tiles.AdsTile;
import org.newstand.datamigration.ui.tiles.FTPControlTile;
import org.newstand.datamigration.ui.tiles.FTPFullAnoTile;
import org.newstand.datamigration.ui.tiles.FTPFullWakeLockTile;
import org.newstand.datamigration.ui.tiles.FTPPWDTile;
import org.newstand.datamigration.ui.tiles.FTPRootDirTile;
import org.newstand.datamigration.ui.tiles.FTPUserNameTile;
import org.newstand.datamigration.ui.tiles.ThemedCategory;
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

public class FTPServerNavigatorFragment extends DashboardFragment implements Producer<Integer> {

    public static FTPServerNavigatorFragment create() {
        return new FTPServerNavigatorFragment();
    }

    @Getter
    private View rootView;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_home_ftp_server;
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

        actions.addTile(new FTPControlTile(getContext()));
        actions.addTile(new FTPUserNameTile(getContext()));
        actions.addTile(new FTPPWDTile(getContext()));

        Category actions2 = new ThemedCategory();
        actions2.addTile(new FTPFullAnoTile(getContext()));
        actions2.addTile(new FTPFullWakeLockTile(getContext()));
        actions2.addTile(new FTPRootDirTile(getContext()));

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
        findView(rootView, R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitionTo(new Intent(getContext(), HelpActivity.class));
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    protected void transitionTo(Intent intent) {
        TransitionSafeActivity transitionSafeActivity = (TransitionSafeActivity) getActivity();
        transitionSafeActivity.transitionTo(intent);
    }

    @Override
    public Integer produce() {
        return R.string.title_ftp;
    }
}
