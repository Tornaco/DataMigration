package org.newstand.datamigration.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.norbsoft.typefacehelper.TypefaceHelper;

import org.newstand.datamigration.R;

public class NavigatorActivity extends TransactionSafeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigator);

        TypefaceHelper.typeface(this);

        setTitle(getTitle());

        findViewById(R.id.card_1).findViewById(android.R.id.button1)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(NavigatorActivity.this, AndroidCategoryViewerActivity.class));
                    }
                });

        findViewById(R.id.card_2).findViewById(android.R.id.button1)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(NavigatorActivity.this, BackupSessionPickerActivity.class));
                    }
                });

        findViewById(R.id.card_3).findViewById(android.R.id.button1)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(NavigatorActivity.this, WifiSetupActivity.class));
                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
}
