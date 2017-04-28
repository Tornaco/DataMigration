package org.newstand.datamigration.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.Producer;
import org.newstand.datamigration.policy.ExtraDataRule;
import org.newstand.datamigration.repo.ExtraDataRulesRepoService;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.ui.tiles.ExtraDirTile;
import org.newstand.datamigration.ui.tiles.PackageNameTile;
import org.newstand.datamigration.ui.widget.ProgressDialogCompat;

import java.util.List;

import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;

/**
 * Created by Nick@NewStand.org on 2017/4/21 19:29
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class RulesCreatorFragment extends DashboardFragment {

    private ExtraDataRule extraDataRule;
    private Producer<String> pkgProducer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        FloatingActionButton fab = null;
        if (root != null) {
            fab = (FloatingActionButton) root.findViewById(R.id.fab);
        }
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFabClick(v);
                }
            });
        }
        return root;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        pkgProducer = (Producer<String>) context;
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.task_viewer, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_remove) {
            if (extraDataRule != null) {
                final ProgressDialog d = ProgressDialogCompat.createUnCancelableIndeterminateShow(getActivity());
                SharedExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        ExtraDataRulesRepoService.get().delete(getContext(), extraDataRule);
                        d.dismiss();
                        getActivity().finish();
                    }
                });
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadOrCreateParams();
    }

    private void loadOrCreateParams() {
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (pkgProducer.produce() != null) {
                            extraDataRule = ExtraDataRulesRepoService.get().findByPkg(getContext(), pkgProducer.produce());
                        } else {
                            extraDataRule = ExtraDataRule.builder().build();
                        }

                        buildUI(getContext());
                    }
                });
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_extra_rules_creator;
    }

    @Override
    protected void onCreateDashCategories(List<Category> categories) {
        super.onCreateDashCategories(categories);

        if (extraDataRule == null) {
            return;
        }

        Category c = new Category();

        PackageNameTile packageNameTile = new PackageNameTile(getActivity(), extraDataRule);
        ExtraDirTile extraDirTile = new ExtraDirTile(getActivity(), extraDataRule);

        c.addTile(packageNameTile);
        c.addTile(extraDirTile);

        categories.add(c);
    }

    private void onFabClick(View view) {
        if (!validateRule()) return;
        final ProgressDialog d = ProgressDialogCompat.createUnCancelableIndeterminateShow(getActivity());

        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                ExtraDataRulesRepoService.get().updateOrInsert(getContext(), extraDataRule);
                d.dismiss();

                getActivity().finish();
            }
        });
    }

    private boolean validateRule() {
        return extraDataRule != null && extraDataRule.getPackageName() != null
                && extraDataRule.getExtraDataDirs() != null;
    }
}
