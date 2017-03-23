package org.newstand.datamigration.worker.backup.session;

import android.icu.text.SimpleDateFormat;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import lombok.Getter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/9 14:23
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

@Getter
@ToString
public class Session implements Parcelable {

    private String name;

    private Session(String name) {
        this.name = name;
    }

    private Session() {
        long startTimeMills = System.currentTimeMillis();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            name = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date(startTimeMills));
        } else {
            name = new java.text.SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date(startTimeMills));
        }
    }

    protected Session(Parcel in) {
        name = in.readString();
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

    public static Session random() {
        Session s = new Session();
        s.name = "Random_" + s.name;
        return s;
    }

    public static Session from(String name) {
        return new Session(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }
}
