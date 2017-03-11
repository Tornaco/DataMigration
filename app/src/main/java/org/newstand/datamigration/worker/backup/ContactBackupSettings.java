package org.newstand.datamigration.worker.backup;

import org.newstand.datamigration.model.ContactRecord;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/9 11:59
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class ContactBackupSettings extends BackupSettings {

    public static final String SUBFIX = ".vcf";

    private ContactRecord[] dataRecord;
    private String destPath;
}
