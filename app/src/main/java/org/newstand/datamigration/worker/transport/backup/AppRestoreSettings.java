package org.newstand.datamigration.worker.transport.backup;

import org.newstand.datamigration.data.model.AppRecord;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/4/6 9:38
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AppRestoreSettings extends RestoreSettings {
    private AppRecord appRecord;

    private String sourceDataPath;
    private String destDataPath;

    private String sourceApkPath;

    private String extraSourceDataPath;

    private boolean installApp;
    private boolean installData;
}
