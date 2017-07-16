package org.newstand.datamigration.data.model;

import android.os.Parcelable;

import java.io.IOException;
import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/13 12:00
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class SMSRecord extends FileBasedRecord implements Serializable {

    public static final long serialVersionUID = 3172693911408082974L;

    private String addr;
    private String msg;
    private String readState;
    private String time;

    private MsgBox msgBox;

    @Override
    public DataCategory category() {
        return DataCategory.Sms;
    }

    @Override
    public long calculateSize() throws IOException {
        if (getPath() == null) {
            // A sms backup file is estimated to 48kb.
            return 48 * 1024;
        }
        return super.calculateSize();
    }
}
