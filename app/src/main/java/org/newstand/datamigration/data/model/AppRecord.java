package org.newstand.datamigration.data.model;

import android.graphics.drawable.Drawable;
import android.os.Parcel;

import com.google.gson.Gson;

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
public class AppRecord extends FileBasedRecord {

    public static final String APK_FILE_PREFIX = ".apk";
    public static final String APK_META_PREFIX = ".meta";

    private String pkgName;
    private String versionName;

    private boolean hasApk, hasData, hasExtraData;

    private boolean handleApk, handleData;

    @Deprecated
    private transient Drawable icon;
    private transient String iconUrl;

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static AppRecord fromJson(String json) {
        return new Gson().fromJson(json, AppRecord.class);
    }

    @Override
    public DataCategory category() {
        return DataCategory.App;
    }
}
