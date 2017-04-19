package org.newstand.datamigration.worker.transport.backup;

import org.newstand.datamigration.data.model.CallLogRecord;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/4/19 14:04
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class CallLogRestoreSettings extends RestoreSettings {
    private CallLogRecord callLogRecord;
}
