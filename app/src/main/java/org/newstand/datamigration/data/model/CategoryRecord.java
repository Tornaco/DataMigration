package org.newstand.datamigration.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/7 18:29
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

@Setter
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
public class CategoryRecord extends DataRecord implements Parcelable {
    private DataCategory category;
    private String summary;

    private CategoryRecord(Parcel in) {
        super(in);
        category = DataCategory.valueOf(DataCategory.class, in.readString());
        summary = in.readString();
    }

    public static final Creator<CategoryRecord> CREATOR = new Creator<CategoryRecord>() {
        @Override
        public CategoryRecord createFromParcel(Parcel in) {
            return new CategoryRecord(in);
        }

        @Override
        public CategoryRecord[] newArray(int size) {
            return new CategoryRecord[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(category.name());
        dest.writeString(summary);
    }

    @Override
    public DataCategory category() {
        return category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryRecord that = (CategoryRecord) o;

        return category == that.category;

    }

    @Override
    public int hashCode() {
        return category.hashCode();
    }
}