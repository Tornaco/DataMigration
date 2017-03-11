package org.newstand.datamigration.model;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/7 13:24
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class MiltMediaRecord extends DataRecord implements Parcelable {

    private long size;
    private String path;

    MiltMediaRecord(Parcel in) {
        super(in);
        size = in.readLong();
        path = in.readString();
    }

    public static final Creator<MiltMediaRecord> CREATOR = new Creator<MiltMediaRecord>() {
        @Override
        public MiltMediaRecord createFromParcel(Parcel in) {
            return new MiltMediaRecord(in);
        }

        @Override
        public MiltMediaRecord[] newArray(int size) {
            return new MiltMediaRecord[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(size);
        dest.writeString(path);
    }
}
