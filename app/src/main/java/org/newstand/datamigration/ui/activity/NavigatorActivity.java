package org.newstand.datamigration.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.newstand.datamigration.R;

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

    private void showCard1Pop(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_backup:
                        transitionTo(new Intent(NavigatorActivity.this, AndroidCategoryViewerActivity.class), true);
                        break;
                    case R.id.action_restore:
                        transitionTo(new Intent(NavigatorActivity.this, BackupSessionPickerActivity.class), true);
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
                        transitionTo(new Intent(NavigatorActivity.this, WFDDataSenderActivity.class), true);
                        break;
                    case R.id.action_receive:
                        transitionTo(new Intent(NavigatorActivity.this, WFDDataReceiverActivity.class), true);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            transitionTo(new Intent(this, SettingsActivity.class), true);
        }

        return super.onOptionsItemSelected(item);
    }
}
