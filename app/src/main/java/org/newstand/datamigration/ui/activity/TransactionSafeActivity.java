package org.newstand.datamigration.ui.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.norbsoft.typefacehelper.TypefaceHelper;

public class TransactionSafeActivity extends AppCompatActivity {

    private static final long UI_TRANSACTION_TIME_MILLS = 500;

    protected Fragment mShowingFragment;

    private boolean mIsDestroyed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (needSmoothHook()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onSmoothHook();
                }
            }, UI_TRANSACTION_TIME_MILLS);
        }
    }

    protected void showHomeAsUp() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(TypefaceHelper.typeface(title));
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(TypefaceHelper.typeface(this, titleId));
    }

    protected void onSmoothHook() {

    }

    protected boolean needSmoothHook() {
        return false;
    }

    protected boolean isDestroyedCompat() {
        return mIsDestroyed;
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T findView(@IdRes int resId) {
        return (T) findViewById(resId);
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

        if (isDestroyed() || f == null) {
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
                    .replace(containerId, f).commit();
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
            getSupportFragmentManager().beginTransaction().remove(f).commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .remove(f)
                    .commit();//TODO Ignore the result?
        }
        mShowingFragment = null;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroyed = true;
    }
}
