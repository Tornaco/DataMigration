package org.newstand.datamigration.ui.fragment;

import android.content.Context;
import android.support.annotation.NonNull;

import org.newstand.datamigration.R;
import org.newstand.datamigration.cache.LoadingCacheManager;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.TransportListener;
import org.newstand.datamigration.worker.transport.backup.DataBackupManager;
import org.newstand.datamigration.worker.transport.backup.TransportType;

import java.util.Collection;

/**
 * Created by Nick@NewStand.org on 2017/3/15 16:29
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

// Importing delegate BK
public class DataImportManageFragment extends DataTransportManageFragment {

    public interface LoaderSourceProvider {
        LoaderSource onRequestLoaderSource();
    }

    private LoaderSourceProvider mLoaderSourceProvider;

    @Override
    TransportType getTransportType() {
        return TransportType.Restore;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mLoaderSourceProvider = (LoaderSourceProvider) getActivity();
    }

    @Override
    protected Session onCreateSession() {
        return mLoaderSourceProvider.onRequestLoaderSource().getSession();
    }

    private LoadingCacheManager getCache(LoaderSource source) {
        switch (source.getParent()) {
            case Android:
                return LoadingCacheManager.droid();
            case Backup:
                return LoadingCacheManager.bk();
            case Received:
                return LoadingCacheManager.received();
            default:
                throw new IllegalArgumentException("Bad source:" + source);
        }
    }

    @Override
    protected void readyToGo() {
        super.readyToGo();

        final LoadingCacheManager cache = getCache(mLoaderSourceProvider.onRequestLoaderSource());

        final DataBackupManager dataBackupManager = DataBackupManager.from(getContext(), getSession());

        final TransportListener listener = onCreateTransportListener();

        DataCategory.consumeAllInWorkerThread(new Consumer<DataCategory>() {
            @Override
            public void accept(@NonNull DataCategory category) {
                Collection<DataRecord> dataRecords = cache.checked(category);
                if (Collections.isNullOrEmpty(dataRecords)) {
                    return;
                }

                dataBackupManager.performRestore(dataRecords, category, listener);
            }
        }, new Runnable() {
            @Override
            public void run() {
                enterState(STATE_TRANSPORT_END);
            }
        });
    }

    @Override
    int getStartTitle() {
        return R.string.title_restore_importing;
    }

    @Override
    int getCompleteTitle() {
        return R.string.title_restore_import_complete;
    }

    @Override
    String onCreateCompleteSummary() {
        return "";//FIXME
    }

}
