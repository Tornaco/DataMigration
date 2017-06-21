package org.newstand.datamigration.worker.transport.backup;

import org.newstand.datamigration.data.model.SettingsRecord;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick on 2017/6/21 16:04
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class SystemSettingsBackupSettings extends BackupSettings {
    public static final String SUBFIX = ".system";
    private String destPath;
    private SettingsRecord record;
}
