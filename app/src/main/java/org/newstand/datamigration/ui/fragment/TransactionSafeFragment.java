package org.newstand.datamigration.ui.fragment;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by Nick@NewStand.org on 2017/3/15 9:42
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class TransactionSafeFragment extends Fragment {

    @SuppressWarnings("unchecked")
    protected <T extends View> T findView(View root, @IdRes int idRes) {
        return (T) root.findViewById(idRes);
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    protected <T extends View> T findView(@IdRes int idRes) {
        return (T) getView().findViewById(idRes);
    }
}
