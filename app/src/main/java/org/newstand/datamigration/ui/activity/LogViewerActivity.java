package org.newstand.datamigration.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.ui.widget.ProgressDialogCompat;
import org.newstand.datamigration.utils.Files;

import java.io.File;

/**
 * Created by Nick@NewStand.org on 2017/3/29 16:52
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class LogViewerActivity extends TransitionSafeActivity {

    private String logPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        resolveIntent();

        super.onCreate(savedInstanceState);

        showHomeAsUp();

        setContentView(R.layout.layout_log_viewer);

        setupView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.log_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_view_with_other_app) {

            if (logPath == null) return false;

            Uri u = Files.getUriForFile(this, new File(logPath));

            if (u == null) return false;

            // FIXME Not works~
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(u, "text/plain");

            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupView() {
        final TextView t = findView(R.id.log_view);

        final ProgressDialog d = ProgressDialogCompat.createUnCancelableIndeterminateShow(LogViewerActivity.this);

        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final String msg = Files.readString(logPath);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        t.setText(msg);

                        ProgressDialogCompat.dismiss(d);
                    }
                });
            }
        });
    }

    private void resolveIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            logPath = intent.getStringExtra(IntentEvents.KEY_LOG_PATH);
        }
    }

}
