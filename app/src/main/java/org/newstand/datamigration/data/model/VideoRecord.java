package org.newstand.datamigration.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/7 13:24
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@NoArgsConstructor
@ToString(callSuper = true)
public class VideoRecord extends FileBasedRecord implements Parcelable {
    private VideoRecord(Parcel in) {
        super(in);
    }

    @Override
    public DataCategory category() {
        return DataCategory.Video;
    }
}
