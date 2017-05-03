package org.newstand.datamigration.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.newstand.datamigration.R;
import org.newstand.datamigration.ui.widget.ProgressWheel;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/29 13:25
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class DataTransportUIFragment extends StateBasedFragment {

    @Getter
    private ProgressWheel progressBar;
    @Getter
    private TextView consoleTitleView, consoleSummaryView, consoleDoneButton, consoleLoggerButton;
    @Getter
    private CardView consoleCardView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.data_transporter, container, false);
        progressBar = (ProgressWheel) root.findViewById(R.id.progress_view);
        progressBar.setKeepScreenOn(true);
        consoleCardView = (CardView) root.findViewById(R.id.console_card);
        consoleTitleView = (TextView) root.findViewById(android.R.id.title);
        consoleSummaryView = (TextView) root.findViewById(android.R.id.text1);
        consoleDoneButton = (TextView) root.findViewById(R.id.button);
        consoleLoggerButton = (TextView) root.findViewById(R.id.log);
        return root;
    }
}
