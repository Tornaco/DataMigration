package org.newstand.datamigration.repo;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.reflect.TypeToken;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.event.TransportEventRecord;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.backup.TransportType;
import org.newstand.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TransportEventRecordRepoService extends GsonBasedRepoService<TransportEventRecord> {


    private static final String DATA_FILE_NAME_FORMAT = "Records_%s.eve";

    public static TransportEventRecordRepoService from(Session session, TransportType transportType) {
        return new TransportEventRecordRepoService(session, transportType);
    }

    public TransportEventRecordRepoService(Session session, TransportType transportType) {
        this.filePath =
                SettingsProvider.getBackupSessionDir(session)
                        + File.separator +
                        "Transports"
                        + File.separator
                        + String.format(DATA_FILE_NAME_FORMAT, transportType.name());
    }

    @Override
    public boolean drop() {
        Logger.d("Dropping:%s", filePath);
        return (!new File(filePath).exists()) || new File(filePath).delete();
    }

    public List<TransportEventRecord> succeed(Context context, final DataCategory dataCategory) {
        List<TransportEventRecord> all = findAll(context);
        final List<TransportEventRecord> success = new ArrayList<>();
        Collections.consumeRemaining(all, new Consumer<TransportEventRecord>() {
            @Override
            public void accept(@NonNull TransportEventRecord transportEventRecord) {
                if (transportEventRecord.isSuccess() && transportEventRecord.getCategory() == dataCategory) {
                    success.add(transportEventRecord);
                }
            }
        });
        all.clear();
        all = null;
        return success;
    }


    public List<TransportEventRecord> fails(Context context, final DataCategory dataCategory) {
        List<TransportEventRecord> all = findAll(context);
        final List<TransportEventRecord> fails = new ArrayList<>();
        Collections.consumeRemaining(all, new Consumer<TransportEventRecord>() {
            @Override
            public void accept(@NonNull TransportEventRecord transportEventRecord) {
                if (!transportEventRecord.isSuccess() && transportEventRecord.getCategory() == dataCategory) {
                    fails.add(transportEventRecord);
                }
            }
        });
        all.clear();
        all = null;
        return fails;
    }

    public List<TransportEventRecord> allOf(Context context, final DataCategory dataCategory) {
        List<TransportEventRecord> all = findAll(context);
        final List<TransportEventRecord> allOf = new ArrayList<>();
        Collections.consumeRemaining(all, new Consumer<TransportEventRecord>() {
            @Override
            public void accept(@NonNull TransportEventRecord transportEventRecord) {
                if (transportEventRecord.getCategory() == dataCategory) {
                    allOf.add(transportEventRecord);
                }
            }
        });
        all.clear();
        all = null;
        return allOf;
    }


    @Override
    protected String dataFileName() {
        return null;
    }

    @Override
    protected Class<TransportEventRecord> getClz() {
        return TransportEventRecord.class;
    }

    @Override
    protected boolean isSame(TransportEventRecord old, TransportEventRecord now) {
        return old.getWhen() == now.getWhen();
    }

    @Override
    protected TypeToken onCreateTypeToken() {
        return new TypeToken<ArrayList<TransportEventRecord>>() {
        };
    }
}
