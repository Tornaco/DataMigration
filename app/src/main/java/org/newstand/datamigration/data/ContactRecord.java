package org.newstand.datamigration.data;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/7 10:57
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class ContactRecord extends DataRecord implements Parcelable {
    private String email;
    private String phoneNum;
    private String url;

    private ContactRecord(Parcel in) {
        super(in);
        email = in.readString();
        phoneNum = in.readString();
        url = in.readString();
    }

    public static final Creator<ContactRecord> CREATOR = new Creator<ContactRecord>() {
        @Override
        public ContactRecord createFromParcel(Parcel in) {
            return new ContactRecord(in);
        }

        @Override
        public ContactRecord[] newArray(int size) {
            return new ContactRecord[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(email);
        dest.writeString(phoneNum);
        dest.writeString(url);
    }
}
