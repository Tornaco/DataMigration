package org.newstand.datamigration.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.newstand.datamigration.DataMigrationApp;
import org.newstand.datamigration.R;
import org.newstand.datamigration.repo.BKSessionRepoServiceOneTime;
import org.newstand.datamigration.worker.backup.session.Session;

public class NavigatorActivity extends TransitionSafeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigator);

        setTitle(getTitle());

        findView(R.id.card_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCard1Pop(findView(v, android.R.id.text2));
            }
        });

        findViewById(R.id.card_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCard2Pop(findView(v, android.R.id.text2));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        queryShowHistory();
    }

    private void queryShowHistory() {
        TextView tv1 = findView(findView(R.id.card_1), android.R.id.text2);
        Session last = BKSessionRepoServiceOneTime.get().findLast();
        String intro;
        if (last == null) {
            intro = getString(R.string.title_backup_history_noop);
        } else {
            intro = getString(R.string.title_backup_history, last.getName());
        }
        tv1.setText(intro);

    }

    private void showCard1Pop(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_backup:
                        transitionTo(new Intent(NavigatorActivity.this, AndroidCategoryViewerActivity.class), false);
                        break;
                    case R.id.action_restore:
                        transitionTo(new Intent(NavigatorActivity.this, BackupSessionPickerActivity.class), false);
                        break;
                }
                return true;
            }
        });
        popup.inflate(R.menu.navigator_card_1);
        popup.show();
    }

    private void showCard2Pop(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_send:
                        transitionTo(new Intent(NavigatorActivity.this, WFDDataSenderActivity.class), false);
                        break;
                    case R.id.action_receive:
                        transitionTo(new Intent(NavigatorActivity.this, WFDDataReceiverActivity.class), false);
                        break;
                }
                return true;
            }
        });
        popup.inflate(R.menu.navigator_card_2);
        popup.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            transitionTo(new Intent(this, SettingsActivity.class), false);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean isMainActivity() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataMigrationApp app = (DataMigrationApp) getApplication();
        app.cleanup();
    }
}
