package org.newstand.datamigration.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Checkable;

import io.realm.annotations.PrimaryKey;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/7 10:00
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
public class DataRecord implements Parcelable, Checkable {

    private String displayName;
    @PrimaryKey
    private String id;
    private boolean isChecked;

    protected DataRecord(Parcel in) {
        displayName = in.readString();
        id = in.readString();
        isChecked = in.readInt() == 1;
    }

    public static final Creator<DataRecord> CREATOR = new Creator<DataRecord>() {
        @Override
        public DataRecord createFromParcel(Parcel in) {
            return new DataRecord(in);
        }

        @Override
        public DataRecord[] newArray(int size) {
            return new DataRecord[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(displayName);
        dest.writeString(id);
        dest.writeInt(isChecked ? 1 : 0);
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }

    public DataCategory category() {
        return DataCategory.App;
    }
}
