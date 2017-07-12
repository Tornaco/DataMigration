package org.newstand.datamigration.repo;

import com.google.gson.reflect.TypeToken;

import org.newstand.datamigration.data.event.TransportEventRecord;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.worker.transport.Session;

import java.io.File;
import java.util.ArrayList;

public class TransportEventRecordRepoService extends GsonBasedRepoService<TransportEventRecord> {

    private String dataFileName;

    public static TransportEventRecordRepoService from(Session session) {
        return new TransportEventRecordRepoService(session.getName());
    }

    public TransportEventRecordRepoService() {
    }

    public TransportEventRecordRepoService(String dataFileName) {
        this.dataFileName = dataFileName;
        this.filePath = SettingsProvider.getCommonDataDir() + File.separator + "Transports" + File.separator + dataFileName();
    }

    @Override
    protected String dataFileName() {
        return this.dataFileName;
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
