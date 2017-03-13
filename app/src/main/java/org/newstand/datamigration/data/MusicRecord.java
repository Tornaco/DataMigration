package org.newstand.datamigration.data;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/7 12:52
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class MusicRecord extends FileBasedRecord implements Parcelable {

    private String artist;
    private long duration;
    private String artUri;

    private MusicRecord(Parcel in) {
        super(in);
        artist = in.readString();
        duration = in.readLong();
        artUri = in.readString();
    }

    public static final Creator<MusicRecord> CREATOR = new Creator<MusicRecord>() {
        @Override
        public MusicRecord createFromParcel(Parcel in) {
            return new MusicRecord(in);
        }

        @Override
        public MusicRecord[] newArray(int size) {
            return new MusicRecord[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(artist);
        dest.writeLong(duration);
        dest.writeString(artUri);
    }
}
