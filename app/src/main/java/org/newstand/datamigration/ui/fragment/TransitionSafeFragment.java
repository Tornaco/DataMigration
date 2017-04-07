package org.newstand.datamigration.ui.fragment;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by Nick@NewStand.org on 2017/3/15 9:42
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class TransitionSafeFragment extends Fragment {

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
        return !isDetached() && isAdded();
    }

    public String getStringSafety(@StringRes int idRes) {
        if (!isAlive()) return null;
        return getString(idRes);
    }

    public final String getStringSafety(@StringRes int resId, Object... formatArgs) {
        if (!isAlive()) return null;
        return getString(resId, formatArgs);
    }
}
