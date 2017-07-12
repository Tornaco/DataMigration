package org.newstand.datamigration.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.newstand.datamigration.R;

import java.util.List;

import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;

public class TransportStatsViewerActivity extends TransitionSafeActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_container_template);
        showHomeAsUp();
        replaceV4(R.id.container, TransportStatsViewerFragment.newInstance(), null);
    }

    public static class TransportStatsViewerFragment extends DashboardFragment {

        public static TransportStatsViewerFragment newInstance() {
            return new TransportStatsViewerFragment();
        }

        @Override
        protected void onCreateDashCategories(List<Category> categories) {
            super.onCreateDashCategories(categories);
        }
    }
}
