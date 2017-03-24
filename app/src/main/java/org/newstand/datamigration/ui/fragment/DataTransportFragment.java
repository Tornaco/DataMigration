package org.newstand.datamigration.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.AbortSignal;
import org.newstand.datamigration.ui.widget.ProgressWheel;
import org.newstand.datamigration.worker.backup.session.Session;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/10 9:30
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class DataTransportFragment extends TransitionSafeFragment {

    @Getter
    protected ProgressWheel progressBar;
    @Getter
    protected TextView consoleTitleView, consoleSummaryView, consoleDoneButton;

    protected final Set<AbortSignal> abortSignals = new HashSet<>();

    protected Session session;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.data_transporter, container, false);
        progressBar = (ProgressWheel) root.findViewById(R.id.progress_view);
        consoleTitleView = (TextView) root.findViewById(android.R.id.title);
        consoleSummaryView = (TextView) root.findViewById(android.R.id.text1);
        consoleDoneButton = (TextView) root.findViewById(R.id.button);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        start();
    }

    protected void start() {

    }
}
