package org.newstand.datamigration.worker.transport.backup;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/8 17:25
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@Setter
@NoArgsConstructor
public class FileRestoreSettings extends RestoreSettings {
    private String sourcePath;
    private String destPath;
}
