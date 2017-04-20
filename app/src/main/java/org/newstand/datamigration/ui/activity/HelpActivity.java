package org.newstand.datamigration.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.common.io.Files;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;

import org.newstand.datamigration.R;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.logger.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Created by Nick@NewStand.org on 2017/4/10 13:44
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class HelpActivity extends TransitionSafeActivity {

    private ProgressRelativeLayout mProgressRelativeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHomeAsUp();
        setContentView(R.layout.help);

        mProgressRelativeLayout = findView(R.id.progress_layout);

        loadAndUpdate();
    }

    private void loadAndUpdate() {

        mProgressRelativeLayout.showLoading();

        String helpUrl = getString(R.string.help_page_url);
        final String fileName = SettingsProvider.getHelpMdFilePath();
        try {
            Files.createParentDirs(new File(fileName));
        } catch (IOException e) {
            Logger.e("Fail to create parent dir %s", Logger.getStackTraceString(e));
            showFallbackHelp();
            return;
        }

        AsyncHttpClient.getDefaultInstance().executeFile(new AsyncHttpGet(helpUrl),
                fileName,
                new AsyncHttpClient.FileCallback() {
                    @Override
                    public void onCompleted(Exception e, AsyncHttpResponse source, File result) {
                        Logger.d("onCompleted %s, %s", e, result);
                        if (e == null) runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressRelativeLayout.showContent();
                            }
                        });
                        else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showFallbackHelp();
                                }
                            });
                        }
                    }
                });

    }

    private void showFallbackHelp() {
        // Load fallback file.
        mProgressRelativeLayout.showContent();
    }
}
