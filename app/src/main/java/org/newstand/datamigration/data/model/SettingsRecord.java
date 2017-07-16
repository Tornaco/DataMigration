package org.newstand.datamigration.data.model;

import java.io.IOException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Nick on 2017/6/21 13:07
 */

@Getter
@Setter
@NoArgsConstructor
public class SettingsRecord extends FileBasedRecord {
    private String key;
    private String value;
    private String namespace;

    @Override
    public DataCategory category() {
        return DataCategory.SystemSettings;
    }

    @Override
    public long calculateSize() throws IOException {
        if (getPath() == null) {
            // A settings backup file is estimated to 48kb.
            return 48 * 1024;
        }
        return super.calculateSize();
    }
}
