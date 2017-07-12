package org.newstand.datamigration.worker.transport;

import android.os.Parcel;
import android.os.Parcelable;

import org.newstand.datamigration.utils.DateUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/9 14:23
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@Setter
@ToString
public class Session implements Parcelable {

    private int id;

    private String name;

    private long date;

    private Session(String name) {
        this.name = name;
        this.date = System.currentTimeMillis();
    }

    private Session(String name, long date) {
        this.name = name;
        this.date = date;
    }

    public Session() {
        long startTimeMills = System.currentTimeMillis();
        name = DateUtils.formatForFileName(startTimeMills);
        date = startTimeMills;
    }

    protected Session(Parcel in) {
        name = in.readString();
        date = in.readLong();
    }

    public static final Creator<Session> CREATOR = new Creator<Session>() {
        @Override
        public Session createFromParcel(Parcel in) {
            return new Session(in);
        }

        @Override
        public Session[] newArray(int size) {
            return new Session[size];
        }
    };

    public static Session create() {
        return new Session();
    }

    public static Session tmp() {
        Session s = new Session();
        s.name = "Tmp_" + s.name;
        return s;
    }

    public boolean isTmp() {
        return name.startsWith("Tmp_");
    }

    public void rename(String name) {
        this.name = name;
    }

    public static Session from(String name) {
        return new Session(name);
    }

    public static Session from(String name, long date) {
        Session s = new Session(name);
        s.date = date;
        return s;
    }

    public static Session from(Session session) {
        if (session == null) return null;
        return new Session(session.getName(), session.getDate());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Session session = (Session) o;

        if (date != session.date) return false;
        return name.equals(session.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (int) (date ^ (date >>> 32));
        return result;
    }
}
