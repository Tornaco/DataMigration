package org.newstand.datamigration.data.model;

import android.os.Environment;

import com.google.gson.Gson;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by Nick@NewStand.org on 2017/4/21 14:19
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class HelpInfoTest {
    @Test
    public void toJson() throws Exception {

        ArrayList<HelpInfo> helpInfos = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            HelpInfo info = new HelpInfo();
            info.setDate(System.currentTimeMillis());
            info.setAnswer("A");
            info.setAsker("A");
            info.setQuestion("Q");
            info.setAskerAvatar("A");
            helpInfos.add(info);
        }

        String content = new Gson().toJson(helpInfos);

        org.newstand.datamigration.utils.Files.writeString(content, Environment.getExternalStorageDirectory().getPath() + "/helps");
    }
}