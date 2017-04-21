package org.newstand.datamigration.ui.fragment;

import org.newstand.datamigration.ui.tiles.ReceiveTile;
import org.newstand.datamigration.ui.tiles.ReceivedViewerTile;
import org.newstand.datamigration.ui.tiles.SendTile;
import org.newstand.datamigration.ui.tiles.ThemedCategory;

import java.util.List;

import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;

/**
 * Created by Nick@NewStand.org on 2017/4/21 11:47
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SenderReceiverActionsFragment extends DashboardFragment {

    public static SenderReceiverActionsFragment create() {
        return new SenderReceiverActionsFragment();
    }

    @Override
    protected void onCreateDashCategories(List<Category> categories) {
        super.onCreateDashCategories(categories);

        Category about = new ThemedCategory();
        about.addTile(new SendTile(getActivity()));
        about.addTile(new ReceiveTile(getActivity()));
        about.addTile(new ReceivedViewerTile(getActivity()));

        categories.add(about);
    }
}
