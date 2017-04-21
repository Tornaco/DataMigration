package org.newstand.datamigration.data.model;

import com.google.gson.Gson;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/4/21 14:02
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@Setter
@ToString
public class HelpInfo {
    private String question, answer, asker, askerAvatar;
    private long date;


    public String toJson() {
        return new Gson().toJson(this);
    }

    public static HelpInfo fromJson(String json) {
        return new Gson().fromJson(json, HelpInfo.class);
    }
}
