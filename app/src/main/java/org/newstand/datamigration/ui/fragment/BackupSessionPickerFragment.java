package org.newstand.datamigration.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.newstand.datamigration.R;
import org.newstand.datamigration.loader.LoaderListenerMainThreadAdapter;
import org.newstand.datamigration.loader.SessionLoader;
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
        adapter = onCreateAdapter();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SessionLoader.loadAsync(new LoaderListenerMainThreadAdapter<Session>() {
            @Override
            public void onCompleteMainThread(Collection<Session> collection) {
                super.onCompleteMainThread(collection);
                getAdapter().update(collection);
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
        };
    }

    public interface OnSessionSelectListener {
        void onSessionSelect(Session session);
    }
}
