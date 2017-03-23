package org.newstand.datamigration.data.model;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/12 20:50
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@NoArgsConstructor
@ToString(callSuper = true)
@Setter
@Getter
public class AppRecord extends FileBasedRecord implements Parcelable {

    private String pkgName;
    private String versionName;

    private transient Drawable icon;

    private AppRecord(Parcel in) {
        super(in);
        pkgName = in.readString();
        versionName = in.readString();
    }

    public static final Creator<AppRecord> CREATOR = new Creator<AppRecord>() {
        @Override
        public AppRecord createFromParcel(Parcel in) {
            return new AppRecord(in);
        }

        @Override
        public AppRecord[] newArray(int size) {
            return new AppRecord[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(pkgName);
        dest.writeString(versionName);
    }

    @Override
    public DataCategory category() {
        return DataCategory.App;
    }
}
