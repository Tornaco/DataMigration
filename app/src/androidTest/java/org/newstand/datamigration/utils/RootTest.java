package org.newstand.datamigration.utils;

import android.os.Environment;
import androidx.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.chrisplus.rootmanager.RootManager;
import com.chrisplus.rootmanager.container.Result;
import com.google.common.io.Files;
import com.stericson.rootools.RootTools;
import com.stericson.rootshell.exceptions.RootDeniedException;
import com.stericson.rootshell.execution.Command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.logger.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Nick@NewStand.org on 2017/4/1 15:20
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class RootTest {

    @Before
    public void requireRoot() {
        RootManager.getInstance().obtainPermission();
    }

    @Test
    public void testAccess() {
        RootManager.getInstance().obtainPermission();
    }

    @Test
    public void testAccessDataData() throws IOException {
        testAccess();
        accessDir("data/data/" + InstrumentationRegistry.getTargetContext().getPackageName());
        String prefPath = "data/data/" + InstrumentationRegistry.getTargetContext().getPackageName()
                + "/shared_prefs/" + InstrumentationRegistry.getTargetContext().getPackageName() + "_preferences.xml";
        catFile(prefPath);
        copyFile(prefPath, SettingsProvider.getTestDir() + "/prefs_copy.xml");

        String targetPath = new File(prefPath).getParent() + "/DEVICEID.TXT";
        copyFile(Environment.getExternalStorageDirectory().getPath()
                + "/deviceid.txt", targetPath);

        Result res = RootManager.getInstance().runCommand("chmod 660 " + targetPath);

        Logger.d("res %s, %s %d", res.getMessage(), res.getResult(), res.getStatusCode());
    }

    public void accessDir(String dir) {
        Collections.consumeRemaining(com.google.common.io.Files.fileTreeTraverser().postOrderTraversal(new File(dir)),
                new Consumer<File>() {
                    @Override
                    public void accept(@NonNull File file) {
                        Logger.d("File:" + file);
                    }
                });
    }

    public void catFile(String file) throws IOException {
        Logger.d("cat:%s", file);

        FileReader fr = new FileReader(new File(file));
        BufferedReader br = new BufferedReader(fr);

        String line;
        while ((line = br.readLine()) != null) {
            Logger.d("line=%s", line);
        }
    }

    public void copyFile(String from, String to) throws IOException {

        Logger.d("CP %s to %s", from, to);

        Files.createParentDirs(new File(to));
        com.google.common.io.Files.copy(new File(from), new File(to));

        Collections.consumeRemaining(com.google.common.io.Files.fileTreeTraverser().children(new File(to).getParentFile()), new Consumer<File>() {
            @Override
            public void accept(@NonNull File file) {
                Logger.d("Child:%s", file.getName());
            }
        });

    }

    @Test
    public void testQQ() throws TimeoutException, RootDeniedException, IOException {
        String qqPkgName = "com.tencent.mobileqq/";
        accessDir("data/data/" + qqPkgName);

        Result res = RootManager.getInstance().runCommand("ls -l " + "data/data/" + qqPkgName);
        printResult(res);

        RootTools.getShell(true).add(new Command(1, "adb shell input keyevent 3"));
    }

    void printResult(Result res) {
        Logger.d("Result %s, %s, %d", res.getMessage(), res.getResult(), res.getStatusCode());
    }
}
