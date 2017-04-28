package org.newstand.datamigration.worker.transport.backup;

import org.newstand.datamigration.data.model.AppRecord;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/4/2 21:51
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class AppBackupSettings extends BackupSettings {
    private AppRecord appRecord;

    private String sourceDataPath;
    private String destDataPath;
    private String destExtraDataPath;

    private String sourceApkPath;
    private String destApkPath;

    private String[] extraDirs;
}
