package org.newstand.datamigration.worker.backup;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/8 17:25
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class FileBackupSettings extends BackupSettings {
    private String sourcePath;
    private String destPath;
}
