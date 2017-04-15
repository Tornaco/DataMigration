package org.newstand.datamigration.worker.transport.backup;

import org.newstand.datamigration.data.model.SMSRecord;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/13 13:52
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class SMSBackupSettings extends BackupSettings {
    private SMSRecord smsRecord;
    private String destPath;
}
