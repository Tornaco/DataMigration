package org.newstand.datamigration.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.common.collect.ImmutableList;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.Producer;
import org.newstand.datamigration.ui.FragmentController;
import org.newstand.datamigration.ui.fragment.BackupRestoreNavigatorFragment;
import org.newstand.datamigration.ui.fragment.SenderReceiverNavigatorFragment;

import java.util.List;

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
        setContentView(R.layout.activity_drawer_navigator);
        handler = new Handler();
        setupView();
        setupFragment();
    }

    @Override
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

        super.setupView();
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
        getMenuInflater().inflate(R.menu.help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_help) {
            transitionTo(new Intent(this, HelpActivity.class));
        }
        return true;
    }

    private void updateTitle() {
        Fragment card = getCardController().getCurrent();
        @SuppressWarnings("unchecked") Producer<Integer> titleProducer = (Producer<Integer>) card;
        // setTitle(titleProducer.produce());
    }
}
