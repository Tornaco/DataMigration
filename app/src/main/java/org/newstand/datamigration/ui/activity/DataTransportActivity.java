package org.newstand.datamigration.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import org.newstand.datamigration.R;
import org.newstand.datamigration.provider.ThemeColor;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/10 9:30
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataTransportActivity extends TransitionSafeActivity {

    @Getter
    @Setter
    private BackEventListener backEventListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (getThemeColor()) {
            case White:
                setContentView(R.layout.activity_with_container_with_appbar_dark_template);

                break;
            default:
                setContentView(R.layout.activity_with_container_with_appbar_template);

                break;
        }
        Toolbar toolbar = findView(R.id.toolbar);
        setSupportActionBar(toolbar);
        showHomeAsUp();
    }

    @Override
    protected void onSmoothHook() {
        super.onSmoothHook();
        replaceV4(R.id.container, getTransportFragment(), null);
    }

    protected Fragment getTransportFragment() {
        return null;
    }

    @Override
    protected boolean needSmoothHook() {
        return true;
    }

    @Override
    protected void onApplyTheme(ThemeColor color) {
        int themeRes = getAppThemeNoActionBar(color);
        setTheme(themeRes);
    }

    @Override
    public void onBackPressed() {
        if (backEventListener != null) {
            backEventListener.onBackEvent();
            return; // We should not finish directly if transporting.
        }
        super.onBackPressed();
    }

    @Override
    protected boolean interruptHomeOption() {
        if (backEventListener != null) {
            backEventListener.onBackEvent();
            return true;
        }
        return super.interruptHomeOption();
    }

    public interface BackEventListener {
        void onBackEvent();
    }
}
