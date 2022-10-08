package org.newstand.datamigration.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import org.newstand.datamigration.R;
import org.newstand.datamigration.loader.LoaderListenerMainThreadAdapter;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.loader.SessionLoader;
import org.newstand.datamigration.repo.ReceivedSessionRepoService;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.ui.adapter.SessionListAdapter;
import org.newstand.datamigration.ui.adapter.SessionListViewHolder;
import org.newstand.datamigration.ui.widget.InputDialogCompat;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.backup.DataBackupManager;
import org.newstand.logger.Logger;

import java.io.File;
import java.util.Collection;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/9 15:53
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ReceivedSessionPickerFragment extends LoadingFragment<Collection<Session>> {

    @Getter
    private RecyclerView recyclerView;

    @Getter
    private SessionListAdapter adapter;

    @Getter
    OnSessionSelectListener onSessionSelectListener;

    @Getter
    private SwipeRefreshLayout swipeRefreshLayout;

    @Getter
    private View rootView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onSessionSelectListener = (OnSessionSelectListener) getActivity();
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.recycler_view_template, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requestLoading();
    }

    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        // recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter = onCreateAdapter();
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestLoading();
            }
        });
    }

    @Override
    void onRequestLoading() {
        SessionLoader.loadAsync(getContext(), LoaderSource.builder().parent(LoaderSource.Parent.Received).build(),
                new LoaderListenerMainThreadAdapter<Session>() {
                    @Override
                    public void onStartMainThread() {
                        super.onStartMainThread();
                        swipeRefreshLayout.setRefreshing(true);
                    }

                    @Override
                    public void onCompleteMainThread(Collection<Session> collection) {
                        super.onCompleteMainThread(collection);
                        loadingComplete(collection);
                    }
                });
    }

    @Override
    void onLoadingComplete(Collection<Session> sessions) {
        getAdapter().update(sessions);
        swipeRefreshLayout.setRefreshing(false);
    }

    private SessionListAdapter onCreateAdapter() {
        return new SessionListAdapter(getActivity()) {
            @Override
            protected void onItemClick(SessionListViewHolder holder) {
                Session session = getAdapter().getSessionList().get(holder.getAdapterPosition());
                onSessionSelectListener.onSessionSelect(session);
            }

            @Override
            protected void onBindViewHolder(final SessionListViewHolder holder, Session r) {
                super.onBindViewHolder(holder, r);

                holder.getActionView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popup = new PopupMenu(getActivity(), v);

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
        Snackbar.make(getRootView(), removed ?
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
                                        boolean res = ReceivedSessionRepoService.get().delete(getContext(), session);
                                    }
                                });

                                break;

                            default:
                                requestLoading();
                                break;
                        }
                    }
                })
                .show();
    }

    protected void showRenameDialog(final Session session) {
        new InputDialogCompat.Builder(getActivity())
                .setTitle(getString(R.string.action_rename))
                .setInputDefaultText(session.getName())
                .setInputMaxWords(32)
                .setPositiveButton(getString(android.R.string.ok), new InputDialogCompat.ButtonActionListener() {
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

    protected boolean validateInput(Session session, CharSequence in) {
        return !TextUtils.isEmpty(in) && (!in.toString().equals(session.getName()))
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
                boolean ok = ReceivedSessionRepoService.get().update(getContext(), worked);
                if (ok) {
                    ok = DataBackupManager.from(getContext()).renameSessionChecked(
                            LoaderSource.builder().parent(LoaderSource.Parent.Received).build(),
                            target, name);
                }
                if (!ok) {
                    worked.setName(prevName);
                    ReceivedSessionRepoService.get().update(getContext(), worked);
                }
                final boolean finalOk = ok;
                post(new Runnable() {
                    @Override
                    public void run() {
                        showRenameResult(worked, finalOk);
                    }
                });
                requestLoading();
            }
        });
    }

    private void showRenameResult(Session session, boolean res) {
        Snackbar.make(getRootView(), res ?
                        getString(R.string.action_renamed_to, session.getName())
                        : getString(R.string.action_rename_fail), Snackbar.LENGTH_LONG)
                .setAction(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Empty.
                    }
                }).show();
    }

    public interface OnSessionSelectListener {
        void onSessionSelect(Session session);
    }
}
