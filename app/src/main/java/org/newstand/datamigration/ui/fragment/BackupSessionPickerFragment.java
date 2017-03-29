package org.newstand.datamigration.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;

import org.newstand.datamigration.R;
import org.newstand.datamigration.loader.LoaderListenerMainThreadAdapter;
import org.newstand.datamigration.loader.SessionLoader;
import org.newstand.datamigration.repo.BKSessionRepoService;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.ui.adapter.SessionListAdapter;
import org.newstand.datamigration.ui.adapter.SessionListViewHolder;
import org.newstand.datamigration.worker.backup.session.Session;

import java.util.Collection;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/9 15:53
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class BackupSessionPickerFragment extends TransitionSafeFragment {

    @Getter
    RecyclerView recyclerView;

    @Getter
    private SessionListAdapter adapter;

    @Getter
    OnSessionSelectListener onSessionSelectListener;

    @Getter
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onSessionSelectListener = (OnSessionSelectListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.recycler_view_template, container, false);
        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter = onCreateAdapter();
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startLoading();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        startLoading();
    }

    private void startLoading() {
        SessionLoader.loadAsync(new LoaderListenerMainThreadAdapter<Session>() {
            @Override
            public void onStartMainThread() {
                super.onStartMainThread();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onCompleteMainThread(Collection<Session> collection) {
                super.onCompleteMainThread(collection);
                getAdapter().update(collection);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
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

    private void onRequestRemove(int position) {
        final Session session = getAdapter().getSessionList().remove(position);
        getAdapter().notifyItemRemoved(position);

        Logger.d("Removing session %s", session);

        if (session != null) {
            SharedExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    boolean res = BKSessionRepoService.get().delete(session);
                    showRemoveResult(session, res);
                }
            });
        }
    }

    private void showRemoveResult(Session session, boolean removed) {
        Snackbar.make(getRecyclerView(), removed ?
                getString(R.string.title_removed, session.getName())
                : getString(R.string.title_remove_failed, session.getName()), Snackbar.LENGTH_LONG)
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
