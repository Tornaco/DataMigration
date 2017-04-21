package org.newstand.datamigration.secure;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.utils.Files;

import java.io.File;

/**
 * Created by Nick@NewStand.org on 2017/4/21 15:57
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class EncryptManagerTest {
    @Test
    public void from() throws Exception {
        Assert.assertTrue(EncryptManager.from(InstrumentationRegistry.getTargetContext()) != null);
    }

    @Test
    public void encrypt() throws Exception {
        EncryptManager encryptManager = EncryptManager.from(InstrumentationRegistry.getTargetContext());

        String fileToTest = SettingsProvider.getTestDir() + "/TestFile";
        String target = SettingsProvider.getTestDir() + "/TestFile_S";

        Files.writeString("Hello world~", fileToTest);

        encryptManager.encrypt(fileToTest, target);

        Assert.assertTrue(new File(target).exists());

        new File(fileToTest).delete();
    }

    @Test
    public void decrypt() throws Exception {
        EncryptManager encryptManager = EncryptManager.from(InstrumentationRegistry.getTargetContext());

        String fileToTest = SettingsProvider.getTestDir() + "/TestFile";
        new File(fileToTest).delete();
        String target = SettingsProvider.getTestDir() + "/TestFile_S";

        encryptManager.decrypt(target, fileToTest);

        Assert.assertTrue(new File(fileToTest).exists());

        Assert.assertTrue("Hello world~".endsWith(Files.readString(fileToTest)));
    }

}