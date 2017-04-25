package org.newstand.datamigration.worker.transport.backup;

import org.newstand.datamigration.data.model.WifiRecord;
import org.newstand.datamigration.worker.transport.backup.BackupSettings;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/4/25 16:41
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class WifiBackupSettings extends BackupSettings {
    public static final String SUBFIX = ".conf";
    private String destPath;
    private WifiRecord record;
}
