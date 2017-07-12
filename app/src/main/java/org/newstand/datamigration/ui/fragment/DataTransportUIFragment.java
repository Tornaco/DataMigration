package org.newstand.datamigration.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.newstand.datamigration.R;
import org.newstand.datamigration.provider.ThemeColor;
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
    ProgressBar bottomProgressBar;
    @Getter
    private TextView consoleTitleView, consoleSummaryView;
    @Getter
    private CardView consoleCardView;

    @Getter
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ThemeColor color = getThemeColor();
        View root;
        switch (color) {
            case White:
                root = inflater.inflate(R.layout.data_transporter_dark, container, false);
                break;
            default:
                root = inflater.inflate(R.layout.data_transporter, container, false);
                break;
        }
        progressBar = (ProgressWheel) root.findViewById(R.id.progress_view);
        progressBar.setKeepScreenOn(true);
        consoleCardView = (CardView) root.findViewById(R.id.console_card);
        consoleTitleView = (TextView) root.findViewById(android.R.id.title);
        consoleSummaryView = (TextView) root.findViewById(android.R.id.text1);
        bottomProgressBar = (ProgressBar) root.findViewById(R.id.progress_bar);
        fab = (FloatingActionButton) root.findViewById(R.id.fab);
        fab.hide();
        return root;
    }
}
