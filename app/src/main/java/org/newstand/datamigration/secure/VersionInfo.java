package org.newstand.datamigration.secure;

import android.support.annotation.Nullable;

import com.google.gson.Gson;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Builder;

/**
 * Created by Nick@NewStand.org on 2017/4/14 16:02
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@Builder
@ToString
public class VersionInfo {

    private int versionCode;
    private String versionName;
    private String downloadUrl;
    private long updateDate;
    private String updateDescription;

    private VersionInfo(int versionCode, String versionName, String downloadUrl, long updateDate, String updateDescription) {
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.downloadUrl = downloadUrl;
        this.updateDate = updateDate;
        this.updateDescription = updateDescription;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Nullable
    public static VersionInfo fromJson(String jsonStr) throws Exception {
        Gson gson = new Gson();
        try {
            return gson.fromJson(jsonStr, VersionInfo.class);
        } catch (Throwable e) {
            throw e;
        }
    }
}
