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

        for (int i = 0; i < 2; i++) {
            Answer answer = new Answer();
            answer.setText("This is content text.");
            answer.setImageUrls(new String[]{
                    "www.abc.com",
                    "www.dew.com",
                    "www.sdf.com",
            });
            HelpInfo info = new HelpInfo();
            info.setDate(System.currentTimeMillis());
            info.setAnswer(answer);
            info.setAsker("A");
            info.setQuestion("Q");
            info.setAskerAvatar("A");
            helpInfos.add(info);
        }

        String content = new Gson().toJson(helpInfos);

        org.newstand.datamigration.utils.Files.writeString(content, Environment.getExternalStorageDirectory().getPath() + "/helps");
    }
}