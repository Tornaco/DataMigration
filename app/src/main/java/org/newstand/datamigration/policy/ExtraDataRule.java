package org.newstand.datamigration.policy;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.StringTokenizer;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Builder;

/**
 * Created by Nick@NewStand.org on 2017/4/28 9:33
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Builder
@Getter
@Setter
public class ExtraDataRule {

    private String alias;
    private String packageName;
    private String extraDataDirs;
    private String versionCodeLimit;
    private String filter;
    private boolean enabled;

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static ExtraDataRule fromJson(String json) {
        return new Gson().fromJson(json, ExtraDataRule.class);
    }

    public String[] parseDir() {
        if (TextUtils.isEmpty(extraDataDirs)) {
            return null;
        }
        StringTokenizer tokenizer = new StringTokenizer(extraDataDirs, ",");

        String[] res = new String[tokenizer.countTokens()];

        for (int i = 0; i < res.length; i++) {
            res[i] = tokenizer.nextToken().trim();
        }

        return res;
    }

    public static boolean validateDir(String extraDataDirs) {
        if (TextUtils.isEmpty(extraDataDirs)) {
            return false;
        }
        StringTokenizer tokenizer = new StringTokenizer(extraDataDirs, ",");
        return tokenizer.countTokens() > 0;
    }
}
