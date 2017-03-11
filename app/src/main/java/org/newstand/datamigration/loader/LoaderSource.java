package org.newstand.datamigration.loader;

import android.os.Parcel;
import android.os.Parcelable;

import org.newstand.datamigration.worker.backup.session.Session;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Builder;

/**
 * Created by Nick@NewStand.org on 2017/3/9 14:46
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Builder
public class LoaderSource implements Parcelable {
    @Getter
    private Session session;
    @Getter
    private Parent parent;

    private LoaderSource(Session session, Parent parent) {
        this.session = session;
        this.parent = parent;
    }

    private LoaderSource(Parcel in) {
        session = in.readParcelable(Session.class.getClassLoader());
        parent = Parent.valueOf(Parent.class, in.readString());
    }

    public static final Creator<LoaderSource> CREATOR = new Creator<LoaderSource>() {
        @Override
        public LoaderSource createFromParcel(Parcel in) {
            return new LoaderSource(in);
        }

        @Override
        public LoaderSource[] newArray(int size) {
            return new LoaderSource[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(session, flags);
        dest.writeString(parent.name());
    }

    public enum Parent {
        ContentProvider,
        Backup
    }
}
