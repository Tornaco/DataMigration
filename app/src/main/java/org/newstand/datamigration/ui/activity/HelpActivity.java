package org.newstand.datamigration.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.HelpInfo;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.ui.adapter.HelpListAdapter;
import org.newstand.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/4/10 13:44
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class HelpActivity extends TransitionSafeActivity {

    private ProgressRelativeLayout mProgressRelativeLayout;

    @Getter
    RecyclerView recyclerView;

    @Getter
    private HelpListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHomeAsUp();
        setContentView(R.layout.help);
        setupView();
        loadAndUpdate();
    }

    private void setupView() {
        mProgressRelativeLayout = findView(R.id.progress_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = onCreateAdapter();
        recyclerView.setAdapter(adapter);
    }

    HelpListAdapter onCreateAdapter() {
        return new HelpListAdapter(this);
    }

    private void loadAndUpdate() {

        mProgressRelativeLayout.showLoading();

        String helpUrl = getString(R.string.help_page_url);
        final String fileName = SettingsProvider.getHelpMdFilePath();
        try {
            Files.createParentDirs(new File(fileName));
        } catch (IOException e) {
            Logger.e("Fail to create parent dir %s", Logger.getStackTraceString(e));
            return;
        }
    }

    private void decodeHelpInfo(final File file) {
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String content = org.newstand.datamigration.utils.Files.readString(file.getPath());
                if (TextUtils.isEmpty(content)) {
                    onError();
                    return;
                }

                try {
                    final ArrayList<HelpInfo> helpInfos = new Gson().fromJson(content, new TypeToken<ArrayList<HelpInfo>>() {
                    }.getType());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressRelativeLayout.showContent();
                            adapter.update(helpInfos);
                        }
                    });
                } catch (Throwable w) {
                    Logger.e("Fail to json %s", Logger.getStackTraceString(w));
                    onError();
                }
            }
        });
    }

    private void onError() {

    }
}
