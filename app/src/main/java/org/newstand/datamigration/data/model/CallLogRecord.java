package org.newstand.datamigration.data.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/4/19 13:35
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class CallLogRecord extends FileBasedRecord {
    private String num;
    private String name;
    private long date;
    private int type;

    @Override
    public DataCategory category() {
        return DataCategory.CallLog;
    }
}
