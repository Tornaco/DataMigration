package org.newstand.datamigration.data;

import android.os.Parcel;
import android.os.Parcelable;

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
public class SMSRecord extends DataRecord implements Serializable, Parcelable {

    public  static final long serialVersionUID =3172693911408082974L;

    private String id;
    private String addr;
    private String msg;
    private String readState;
    private String time;

    private MsgBox msgBox;
    private String path;

    public static final Creator<SMSRecord> CREATOR = new Creator<SMSRecord>() {
        @Override
        public SMSRecord createFromParcel(Parcel in) {
            return new SMSRecord(in);
        }

        @Override
        public SMSRecord[] newArray(int size) {
            return new SMSRecord[size];
        }
    };

    private SMSRecord(Parcel in) {
        super(in);
        id = in.readString();
        addr = in.readString();
        msg = in.readString();
        readState = in.readString();
        time = in.readString();
        msgBox = MsgBox.valueOf(in.readString());
        path = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(id);
        dest.writeString(addr);
        dest.writeString(msg);
        dest.writeString(readState);
        dest.writeString(time);
        dest.writeString(msgBox.name());
        dest.writeString(path);
    }
}
