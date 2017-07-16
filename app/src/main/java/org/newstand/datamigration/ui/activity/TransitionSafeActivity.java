package org.newstand.datamigration.ui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.SmsContentProviderCompat;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.provider.ThemeColor;

import java.util.Observable;
import java.util.Observer;

import lombok.Getter;

public class TransitionSafeActivity extends AppCompatActivity {

    protected static final long UI_TRANSACTION_TIME_MILLS = 300;

    protected Fragment mShowingFragment;

    private boolean mIsDestroyed, mTransitionAnimationEnabled;

    @Getter
    private ThemeColor themeColor = ThemeColor.Default;

    @Getter
    private Observer settingsObserver = new Observer() {
        @Override
        public void update(Observable o, Object arg) {
            updateSettings();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        readSettings();
        applyTheme();

        super.onCreate(savedInstanceState);

        SettingsProvider.observe(settingsObserver);

        if (needSmoothHook()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onSmoothHook();
                }
            }, UI_TRANSACTION_TIME_MILLS);
        }
        setupEnterWindowAnimations();
    }

    protected void applyTheme() {
        onApplyTheme(themeColor);
    }

    protected void onApplyTheme(ThemeColor color) {
        int themeRes = getAppTheme(color);
        setTheme(themeRes);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
    }

    public ViewGroup getContentView() {
        return findView(android.R.id.content);
    }

    private void readSettings() {
        mTransitionAnimationEnabled = SettingsProvider.isTransitionAnimationEnabled();
        themeColor = SettingsProvider.getThemeColor();
    }

    private void updateSettings() {
        mTransitionAnimationEnabled = SettingsProvider.isTransitionAnimationEnabled();
        ThemeColor newColor = SettingsProvider.getThemeColor();
        if (themeColor != newColor) {
            onThemeChange();
        }
    }

    protected void onThemeChange() {
        recreate();
    }

    protected
    @StyleRes
    int getAppTheme(ThemeColor color) {
        switch (color) {
            case CoolApk:
                return R.style.AppTheme_CoolApk;
            case CoolDark:
                return R.style.AppTheme_CoolDark;
            case Amber:
                return R.style.AppTheme_Amber;
            case BlueGrey:
                return R.style.AppTheme_BlueGrey;
            case Brown:
                return R.style.AppTheme_Brown;
            case Pink:
                return R.style.AppTheme_Pink;
            case Teal:
                return R.style.AppTheme_Teal;
            case White:
                return R.style.AppTheme_White;
            case Red:
                return R.style.AppTheme_Red;
            case Purple:
                return R.style.AppTheme_Purple;
            case Default:
            default:
                return R.style.AppTheme;
        }
    }

    protected
    @StyleRes
    int getAppThemeNoActionBar(ThemeColor color) {
        switch (color) {
            case CoolApk:
                return R.style.AppTheme_CoolApk_NoActionBar;
            case Amber:
                return R.style.AppTheme_Amber_NoActionBar;
            case BlueGrey:
                return R.style.AppTheme_BlueGrey_NoActionBar;
            case Brown:
                return R.style.AppTheme_Brown_NoActionBar;
            case CoolDark:
                return R.style.AppTheme_CoolDark_NoActionBar;
            case Pink:
                return R.style.AppTheme_Pink_NoActionBar;
            case Teal:
                return R.style.AppTheme_Teal_NoActionBar;
            case White:
                return R.style.AppTheme_White_NoActionBar;
            case Red:
                return R.style.AppTheme_Red_NoActionBar;
            case Purple:
                return R.style.AppTheme_Purple_NoActionBar;
            case Default:
            default:
                return R.style.AppTheme_NoActionBar;
        }
    }

    protected void showHomeAsUp() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
    }

    protected void onSmoothHook() {

    }

    protected boolean needSmoothHook() {
        return false;
    }

    @SuppressWarnings("unchecked")
    public void transitionTo(Intent i) {
        transitionTo(i, mTransitionAnimationEnabled);
    }

    @SuppressWarnings("unchecked")
    private void transitionTo(Intent i, boolean animate) {
        if (animate && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(this, true);
            ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairs);
            startActivity(i, transitionActivityOptions.toBundle());
        } else {
            startActivity(i);
        }
    }

    protected void setupExitWindowAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.explode));
        }
    }

    protected void setupEnterWindowAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.slide_from_left));
        }
    }

    public boolean isDestroyedCompat() {
        return mIsDestroyed;
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T findView(@IdRes int resId) {
        return (T) findViewById(resId);
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T findView(View root, @IdRes int resId) {
        return (T) root.findViewById(resId);
    }

    /**
     * Show fragment page by replaceV4 the given containerId, if you have data to set
     * give a bundle.
     *
     * @param containerId The id to replaceV4.
     * @param fragment    The fragment to show.
     * @param bundle      The data of the fragment if it has.
     */
    protected boolean replaceV4(final int containerId,
                                Fragment fragment, Bundle bundle) {
        return replaceV4(containerId, fragment, bundle, true);
    }

    /**
     * Show fragment page by replaceV4 the given containerId, if you have data to set
     * give a bundle.
     *
     * @param containerId The id to replaceV4.
     * @param f           The fragment to show.
     * @param bundle      The data of the fragment if it has.
     * @param animate     True if you want to animate the fragment.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected boolean replaceV4(final int containerId,
                                Fragment f, Bundle bundle, boolean animate) {

        if (isDestroyedCompat() || f == null) {
            return false;
        }

        if (bundle != null) {
            f.setArguments(bundle);
        }

        if (!animate) {
            getSupportFragmentManager().beginTransaction()
                    .replace(containerId, f).commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(containerId, f)
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .commit();
        }
        mShowingFragment = f;
        return true;
    }

    /**
     * Remove a fragment that is attached, with animation.
     *
     * @param f The fragment to removeV4.
     * @return True if successfully removed.
     * @see #removeV4(Fragment, boolean)
     */
    protected boolean removeV4(final Fragment f) {
        return removeV4(f, true);
    }

    /**
     * Remove a fragment that is attached.
     *
     * @param f       The fragment to removeV4.
     * @param animate True if you want to animate the fragment.
     * @return True if successfully removed.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected boolean removeV4(final Fragment f, boolean animate) {

        if (!isDestroyed() || f == null) {
            return false;
        }

        if (!animate) {
            getSupportFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .remove(f)
                    .commitAllowingStateLoss();//TODO Ignore the result?
        }
        mShowingFragment = null;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!interruptHomeOption()) {
                finishWithAfterTransition();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean interruptHomeOption() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SettingsProvider.unObserve(settingsObserver);
        mIsDestroyed = true;
    }

    protected void finishWithAfterTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }

    public boolean isMainActivity() {
        return false;
    }
}
