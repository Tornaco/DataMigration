package org.newstand.datamigration.ui.fragment;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import android.view.View;

import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.provider.ThemeColor;
import org.newstand.datamigration.ui.activity.TransitionSafeActivity;
import org.newstand.logger.Logger;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/15 9:42
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class TransitionSafeFragment extends Fragment {

    @Getter
    private ThemeColor themeColor;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        themeColor = SettingsProvider.getThemeColor();
    }

    protected void applyThemeColor() {
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

    public void onHidden() {

    }

    public void onShow() {
        Logger.d("onShow %s", getClass().getSimpleName());
    }
}
