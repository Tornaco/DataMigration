package org.newstand.datamigration.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.common.collect.ImmutableList;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.newstand.datamigration.R;
import org.newstand.datamigration.provider.ThemeColor;
import org.newstand.datamigration.ui.FragmentController;
import org.newstand.datamigration.ui.fragment.BackupRestoreNavigatorFragment;
import org.newstand.datamigration.ui.fragment.SenderReceiverNavigatorFragment;
import org.newstand.datamigration.utils.UserManagerCompat;

import java.util.List;

import io.reactivex.functions.Consumer;
import lombok.Getter;

public class DrawerNavigatorActivity extends BaseNavigatorActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int INDEX_BACKUP_RESTORE = 0;
    private static final int INDEX_SENDER_RECEIVER = 1;

    @Getter
    private FragmentController cardController;

    @Getter
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (getThemeColor()) {
            case White:
                setContentView(R.layout.activity_drawer_navigator_dark);
                break;
            default:
                setContentView(R.layout.activity_drawer_navigator);
                break;
        }
        handler = new Handler();
        showRetention();
        updateUserName();
    }

    protected void updateUserName() {
        TextView textView = (TextView) findViewById(R.id.user_name);
        String name = UserManagerCompat.getCurrentUserName(this);
        if (!TextUtils.isEmpty(name)) {
            textView.setText(name);
        }
    }

    private void requestPerms() {
        RxPermissions rxPermissions = new RxPermissions(DrawerNavigatorActivity.this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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

    private void showRetention() {
        boolean hasBasicPermission = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (!hasBasicPermission) {
            new MaterialStyledDialog.Builder(this)
                    .setTitle(R.string.permission_request)
                    .setDescription(R.string.permission_request_message)
                    .setHeaderDrawable(R.drawable.photo_backup_help_card_header)
                    .withDarkerOverlay(false)
                    .setCancelable(false)
                    .setPositiveText(android.R.string.ok)
                    .setNegativeText(android.R.string.cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            requestPerms();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            finish();
                        }
                    })
                    .show();
        } else {
            onPermissionGrant();
        }
    }

    private void onPermissionNotGrant() {
        finishWithAfterTransition();
    }

    private void onPermissionGrant() {
        setupView();
        setupFragment();
    }

    @Override
    protected void onApplyTheme(ThemeColor color) {
        int themeRes = getAppThemeNoActionBar(color);
        setTheme(themeRes);
    }

    protected void setupView() {

        Toolbar toolbar = findView(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findView(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findView(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_backup_restore);
    }

    protected void setupFragment() {
        final List<? extends Fragment> cards =
                ImmutableList.of(
                        BackupRestoreNavigatorFragment.create(),
                        SenderReceiverNavigatorFragment.create());
        cardController = new FragmentController(getSupportFragmentManager(), cards, R.id.container);
        cardController.setDefaultIndex(INDEX_BACKUP_RESTORE);
        cardController.setCurrent(INDEX_BACKUP_RESTORE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findView(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onThemeChange() {
        finishWithAfterTransition();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_backup_restore:
                getCardController().setCurrent(INDEX_BACKUP_RESTORE);
                updateTitle();
                break;
            case R.id.nav_sender_receiver:
                getCardController().setCurrent(INDEX_SENDER_RECEIVER);
                updateTitle();
                break;
            case R.id.nav_settings:
                // Delay to show.
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        transitionTo(new Intent(DrawerNavigatorActivity.this, SettingsActivity.class));
                    }
                };

                handler.postDelayed(r, UI_TRANSACTION_TIME_MILLS);

                break;
            case R.id.nav_about:
                // Delay to show.
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        transitionTo(new Intent(DrawerNavigatorActivity.this, AboutActivity.class));
                    }
                };

                handler.postDelayed(r2, UI_TRANSACTION_TIME_MILLS);
                break;

            case R.id.nav_in:
                // Delay to show.
                Runnable r3 = new Runnable() {
                    @Override
                    public void run() {
                        transitionTo(new Intent(DrawerNavigatorActivity.this, ComeInActivity.class));
                    }
                };

                handler.postDelayed(r3, UI_TRANSACTION_TIME_MILLS);

                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        switch (getThemeColor()) {
//            case White:
//                getMenuInflater().inflate(R.menu.trend_dark, menu);
//                break;
//            default:
//                getMenuInflater().inflate(R.menu.trend, menu);
//                break;
//        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_trend) {
            transitionTo(new Intent(this, HelpActivity.class));
        }
        return true;
    }

    private void updateTitle() {
    }

    @Override
    public boolean isMainActivity() {
        return true;
    }
}
