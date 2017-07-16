package org.newstand.datamigration.ui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.ActionListener;
import org.newstand.datamigration.data.SmsContentProviderCompat;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.data.model.SystemInfo;
import org.newstand.datamigration.loader.LoaderListenerMainThreadAdapter;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.loader.SessionLoader;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.provider.ThemeColor;
import org.newstand.datamigration.repo.BKSessionRepoService;
import org.newstand.datamigration.service.DataCompressServiceProxy;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.ui.adapter.SessionListAdapter;
import org.newstand.datamigration.ui.adapter.SessionListViewHolder;
import org.newstand.datamigration.ui.fragment.BackupSessionPickerFragment;
import org.newstand.datamigration.ui.widget.InputDialogCompat;
import org.newstand.datamigration.utils.Files;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.backup.DataBackupManager;
import org.newstand.logger.Logger;

import java.io.File;
import java.util.Collection;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/9 15:30
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class BackupSessionPickerActivityCollapsing extends TransitionSafeActivity implements BackupSessionPickerFragment.OnSessionSelectListener {

    @Getter
    private RecyclerView recyclerView;

    @Getter
    private SessionListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrollable_with_recycler);
        Toolbar toolbar = findView(R.id.toolbar);
        setSupportActionBar(toolbar);
        showHomeAsUp();
        setTitle(getTitle());
        setupRecyclerView();
        onRequestLoading();
    }

    @Override
    protected void onApplyTheme(ThemeColor color) {
        int themeRes = getAppThemeNoActionBar(color);
        setTheme(themeRes);
    }


    private void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        // recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        adapter = onCreateAdapter();
        recyclerView.setAdapter(adapter);
    }

    void onRequestLoading() {
        SessionLoader.loadAsync(this, new LoaderListenerMainThreadAdapter<Session>() {
            @Override
            public void onStartMainThread() {
                super.onStartMainThread();
            }

            @Override
            public void onCompleteMainThread(Collection<Session> collection) {
                super.onCompleteMainThread(collection);
                onLoadingComplete(collection);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Let's Restore default SMS app.
        // Both OK if we are not SMS app or not.
        SmsContentProviderCompat.restoreDefSmsAppRetentionCheckedAsync(this);
    }

    void onLoadingComplete(Collection<Session> sessions) {
        getAdapter().update(sessions);
    }

    private SessionListAdapter onCreateAdapter() {
        return new SessionListAdapter(this) {
            @Override
            protected void onItemClick(SessionListViewHolder holder) {
                Session session = getAdapter().getSessionList().get(holder.getAdapterPosition());
                onSessionSelect(session);
            }

            @Override
            protected void onBindViewHolder(final SessionListViewHolder holder, Session r) {
                super.onBindViewHolder(holder, r);

                holder.getActionView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popup = new PopupMenu(BackupSessionPickerActivityCollapsing.this, v);

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.action_remove:
                                        onRequestRemove(holder.getAdapterPosition());
                                        break;
                                    case R.id.action_rename:
                                        onRequestRename(holder.getAdapterPosition());
                                        break;
                                    case R.id.action_compress:
                                        onRequestCompress(holder.getAdapterPosition());
                                        break;
                                    case R.id.action_sys_details:
                                        onRequestDetails(holder.getAdapterPosition());
                                        break;
                                    case R.id.action_backup_event:

                                }
                                return true;
                            }
                        });
                        popup.inflate(R.menu.session_picker);
                        popup.show();
                    }
                });
            }
        };
    }

    private void onRequestDetails(int position) {
        final Session session = getAdapter().getSessionList().get(position);
        String infoJson = Files.readString(SettingsProvider.getBackupSystemInfoPath(session));
        SystemInfo systemInfo = null;
        if (!TextUtils.isEmpty(infoJson)) {
            systemInfo = SystemInfo.fromJson(infoJson);
            Logger.i("System info:%s", systemInfo);
        }

        new MaterialDialog.Builder(this)
                .title(R.string.action_details)
//                .titleColorAttr(R.attr.colorAccent)
                .content(systemInfo == null ? getString(R.string.sys_info_not_found)
                        : buildSystemInfo(systemInfo))
                .positiveText(android.R.string.ok)
                .positiveColorAttr(R.attr.colorAccent)
                .build()
                .show();
    }

    private String buildSystemInfo(SystemInfo systemInfo) {
        return getString(R.string.sys_id, systemInfo.getId()) + "\n" +
                getString(R.string.sys_display, systemInfo.getDisplay()) + "\n" +
                getString(R.string.sys_band, systemInfo.getBrand()) + "\n" +
                getString(R.string.sys_board, systemInfo.getBoard()) + "\n" +
                getString(R.string.sys_bootloader, systemInfo.getBootloader()) + "\n" +
                getString(R.string.sys_device, systemInfo.getDevice()) + "\n" +
                getString(R.string.sys_hw, systemInfo.getHardware()) + "\n" +
                getString(R.string.sys_model, systemInfo.getModel()) + "\n" +
                getString(R.string.sys_manufacturer, systemInfo.getManufacturer()) + "\n" +
                getString(R.string.sys_sdk_int, systemInfo.getSdk()) + "\n" +
                getString(R.string.sys_rel, systemInfo.getRel());
    }

    private void onRequestCompress(int adapterPosition) {
        final Session session = getAdapter().getSessionList().get(adapterPosition);
        final ProgressDialog progressDialog = showCompressDialog();

        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {

                File dir = new File(SettingsProvider.getBackupSessionDir(session));

                final File dest = new File(SettingsProvider.getCompressedRootDir() + File.separator + session.getName() + ".zip");

                new DataCompressServiceProxy(getApplicationContext())
                        .compressAsync(dir.getPath(), dest.getPath(), new ActionListener<Boolean>() {
                            @Override
                            public void onAction(@Nullable final Boolean aBoolean) {
                                if (!isDestroyedCompat())
                                    SharedExecutor.runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!isDestroyedCompat()) {
                                                progressDialog.dismiss();
                                                showCompressResult(dest.getPath(), aBoolean != null && aBoolean);
                                            }
                                        }
                                    });
                            }
                        });
            }
        });
    }

    private ProgressDialog showCompressDialog() {
        ProgressDialog p = new ProgressDialog(this);
        p.setIndeterminate(true);
        p.setTitle(R.string.action_compress);
        p.setMessage(getString(R.string.action_compressing));
        p.setCancelable(false);
//        p.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.action_compress_background),
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Empty.
//                    }
//                });
        p.show();
        return p;
    }

    private void showCompressResult(String dest, boolean ok) {
        if (ok) {
            Snackbar.make(getRecyclerView(), getString(R.string.action_compressed_to, dest), Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.action_compressed_how_to_use, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showHowToUseCompressedTips();
                        }
                    })
                    .show();
        } else {
            Snackbar.make(getRecyclerView(), getString(R.string.action_compress_fail), Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, null)
                    .show();
        }
    }

    private void showHowToUseCompressedTips() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.action_compressed_how_to_use)
                .setMessage(getString(R.string.message_compressed_how_to_use, SettingsProvider.getBackupRootDir()))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Ignored.
                    }
                }).create().show();
    }

    private void onRequestRename(int position) {
        final Session session = getAdapter().getSessionList().remove(position);
        Logger.d("Removing session %s", session);
        showRenameDialog(session);
    }

    private void onRequestRemove(int position) {
        final Session session = getAdapter().getSessionList().remove(position);
        getAdapter().notifyItemRemoved(position);
        showRemoveResult(session, true);
    }

    private void showRemoveResult(final Session session, boolean removed) {
        Snackbar.make(getRecyclerView(), removed ?
                getString(R.string.title_removed, session.getName())
                : getString(R.string.title_remove_failed, session.getName()), Snackbar.LENGTH_LONG)
                .setAction(R.string.title_remove_z, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Empty.
                    }
                })
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        switch (event) {
                            case DISMISS_EVENT_CONSECUTIVE:
                            case DISMISS_EVENT_TIMEOUT:
                            case DISMISS_EVENT_SWIPE:

                                SharedExecutor.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        Logger.d("Removing session %s", session);
                                        boolean res = BKSessionRepoService.get().delete(getApplicationContext(), session);
                                        if (res) {
                                            res = Files.deleteDir(new File(SettingsProvider.getBackupSessionDir(session)));
                                        }
                                        if (!res) {
                                            Logger.e("Fail remove session %s", session);
                                        }
                                    }
                                });

                                break;

                            default:
                                onRequestLoading();
                                break;
                        }
                    }
                })
                .show();
    }

    protected void showRenameDialog(final Session session) {
        new InputDialogCompat.Builder(this)
                .setTitle(getString(R.string.action_rename))
                .setInputDefaultText(session.getName())
                .setInputMaxWords(32)
                .setPositiveButton(getString(android.R.string.ok),
                        new InputDialogCompat.ButtonActionListener() {
                            @Override
                            public void onClick(CharSequence inputText) {
                                renameAsync(session, inputText.toString().replace(" ", ""));
                            }
                        })
                .interceptButtonAction(new InputDialogCompat.ButtonActionIntercepter() {
                    @Override
                    public boolean onInterceptButtonAction(int whichButton, CharSequence inputText) {
                        return whichButton == DialogInterface.BUTTON_POSITIVE
                                && !validateInput(session, inputText);
                    }
                })
                .setNegativeButton(getString(android.R.string.cancel), new InputDialogCompat.ButtonActionListener() {
                    @Override
                    public void onClick(CharSequence inputText) {
                        // Nothing.
                    }
                })
                .show();
    }

    protected boolean validateInput(Session s, CharSequence in) {
        return !TextUtils.isEmpty(in) && (!in.toString().equals(s.getName()))
                && !in.toString().contains("Tmp_")
                && !in.toString().contains(File.separator);
    }

    private void renameAsync(final Session target, final String name) {
        final String prevName = target.getName();
        final Session worked = Session.from(target);
        worked.setName(name);
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                boolean ok = BKSessionRepoService.get().update(getApplicationContext(), worked);
                if (ok) {
                    ok = DataBackupManager.from(getApplicationContext()).renameSessionChecked(
                            LoaderSource.builder().parent(LoaderSource.Parent.Backup).build(),
                            target, name);
                }
                if (!ok) {
                    worked.setName(prevName);
                    BKSessionRepoService.get().update(getApplicationContext(), worked);
                }
                final boolean finalOk = ok;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showRenameResult(worked, finalOk);
                    }
                });
                onRequestLoading();
            }
        });
    }

    private void showRenameResult(Session session, boolean res) {
        Snackbar.make(getRecyclerView(), res ?
                getString(R.string.action_renamed_to, session.getName())
                : getString(R.string.action_rename_fail), Snackbar.LENGTH_LONG)
                .setAction(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Empty.
                    }
                }).show();
    }

    @Override
    public void onSessionSelect(Session session) {
        Intent intent = new Intent(this, BackupCategoryViewerActivityCollapsing.class);
        intent.putExtra(IntentEvents.KEY_SOURCE, LoaderSource.builder()
                .parent(LoaderSource.Parent.Backup).session(session).build());
        transitionTo(intent);
    }
}
