package org.newstand.datamigration.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.data.event.TransportEventRecord;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.repo.TransportEventRecordRepoService;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.adapter.CommonListViewHolder;
import org.newstand.datamigration.ui.widget.PermissionMissingDialog;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.backup.TransportType;
import org.newstand.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Created by Tornaco on 2017/7/14.
 * Licensed with Apache.
 */

public class TransportStatsDetailsViewerActivity extends TransitionSafeActivity {

    @Getter
    private Session session;

    @Getter
    private TransportType transportType;

    @Getter
    private RecyclerView recyclerView;

    @Getter
    private SwipeRefreshLayout swipeRefreshLayout;

    @Getter
    private CommonListAdapter adapter;

    @Getter
    private final List<DataRecord> records = new ArrayList<>();

    @Getter
    private DataCategory category;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_template);
        showHomeAsUp();
        resolveIntent();
        setupView();
        showRetention();
    }

    private void resolveIntent() {
        Intent intent = getIntent();
        this.session = intent.getParcelableExtra(IntentEvents.KEY_SOURCE);
        this.category = DataCategory.valueOf(intent.getStringExtra(IntentEvents.KEY_CATEGORY));
        this.transportType = TransportType.valueOf(intent.getStringExtra(IntentEvents.KEY_TRANSPORT_TYPE));
        setTitle(getString(category.nameRes()));
    }

    private void setupView() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.polluted_waves));
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        // recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter = onCreateAdapter();
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showRetention();
            }
        });
    }

    private void showRetention() {
        boolean hasBasicPermission = true;

        for (String perm : PERMISSIONS) {
            boolean hasOne = ContextCompat.checkSelfPermission(getApplicationContext(), perm) == PackageManager.PERMISSION_GRANTED;
            if (!hasOne) {
                hasBasicPermission = false;
                break;
            }
        }

        if (!hasBasicPermission) {
            new MaterialStyledDialog.Builder(this)
                    .setTitle(R.string.permission_request)
                    .setDescription(R.string.permission_request_message)
                    .setHeaderDrawable(R.drawable.photo_backup_help_card_header)
                    .withDarkerOverlay(false)
                    .setCancelable(false)
                    .setPositiveText(android.R.string.ok)
                    .setNegativeText(android.R.string.cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            startLoadingChecked();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            finish();
                        }
                    })
                    .show();
        } else {
            startLoading();
        }
    }

    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.READ_SMS
    };


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void startLoadingChecked() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(PERMISSIONS)
                .subscribe(new io.reactivex.functions.Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) {
                            startLoading();
                        } else {
                            onPermissionNotGrant();
                        }
                    }
                });
    }

    private void onPermissionNotGrant() {
        PermissionMissingDialog.attach(this);
        startLoading();
    }

    private void startLoading() {
        onStartLoading();
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final TransportEventRecordRepoService eventRecordRepoService
                        = TransportEventRecordRepoService.from(getSession(), getTransportType());

                List<TransportEventRecord> eventRecords = onQueryEvents(eventRecordRepoService, category);

                getRecords().clear();

                Collections.consumeRemaining(eventRecords, new Consumer<TransportEventRecord>() {
                    @Override
                    public void accept(@NonNull TransportEventRecord transportEventRecord) {
                        Logger.v("Adding event to adapter:%s", transportEventRecord);
                        getRecords().add(transportEventRecord);
                    }
                });

                postOnLoadComplete();
            }
        });
    }

    private void onStartLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    private void postOnLoadComplete() {
        if (!isDestroyedCompat()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                    postAdapterUpdate();
                }
            });
        }
    }

    private void postAdapterUpdate() {
        if (!isDestroyedCompat()) {

            boolean empty = getRecords().size() == 0;
            if (empty) onNoDataLoaded();

            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getAdapter().update(getRecords());
                }
            });
        }
    }

    private void onNoDataLoaded() {

    }

    protected CommonListAdapter onCreateAdapter() {
        return new CommonListAdapter(this) {

            @Override
            protected int getTemplateLayoutRes() {
                return R.layout.data_item_template_with_checkable_single_line;
            }

            @Override
            protected void onBindViewHolder(CommonListViewHolder holder, DataRecord r) {
                holder.getLineOneTextView().setText(r.getDisplayName());
                holder.getCheckableImageView().setImageDrawable(ContextCompat.getDrawable(getContext(),
                        getCategory().iconRes()));
            }

            @Override
            protected void onItemClick(CommonListViewHolder holder) {
                int position = holder.getAdapterPosition();
                if (position < 0) {
                    // This is a workaround to fix the issue, when adapter position is -1.
                    // which means before/under init???
                    return;
                }
                DataRecord record = getAdapter().getDataRecords().get(position);
                onRecordSelect(record);
            }
        };
    }

    protected void onRecordSelect(DataRecord r) {

    }

    protected List<TransportEventRecord> onQueryEvents(TransportEventRecordRepoService service, DataCategory category) {
        return service.succeed(getApplicationContext(), category);
    }

}
