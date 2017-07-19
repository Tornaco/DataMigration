package org.newstand.datamigration.net;

import com.google.common.io.Files;

import org.newstand.datamigration.data.model.AppRecord;
import org.newstand.datamigration.net.protocol.Acknowledge;
import org.newstand.datamigration.net.protocol.FileHeader;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.utils.BlackHole;
import org.newstand.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.Getter;

import static org.newstand.datamigration.utils.Bytes.createBuffer;

/**
 * Created by Nick@NewStand.org on 2017/3/22 11:20
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

@Getter
public class DataRecordReceiver implements Receiver<ReceiveSettings> {

    private DataRecordReceiver(InputStream in, OutputStream os) {
        this.in = in;
        this.os = os;
    }

    public static DataRecordReceiver with(InputStream in, OutputStream os) {
        return new DataRecordReceiver(in, os);
    }

    private InputStream in;
    private OutputStream os;

    @Override
    public int receive(ReceiveSettings settings) throws IOException {

        FileHeader fileHeader = FileHeader.from(in);

        Logger.i("Receiving: %s", fileHeader.toString());

        Acknowledge.okTo(os);

        String fileName = fileHeader.getFileName();

        // Notify the name of this file.
        settings.getNameConsumer().accept(fileName);

        boolean isEncryptedName = SettingsProvider.isEncryptedName(fileName);

        long size = fileHeader.getSize();

        String destPath;

        // Apply dir settings.
        switch (settings.getCategory()) {
            case App:
                destPath = settings.getRootDir()
                        + File.separator + fileName
                        + File.separator + SettingsProvider.getBackupAppApkDirName()
                        + File.separator + fileName + AppRecord.APK_FILE_PREFIX;// DMBK2/APP/Phone/apk/XX.apk
                break;
            default:
                destPath = settings.getRootDir() + File.separator + fileName;
                break;
        }

        if (isEncryptedName) {
            destPath = SettingsProvider.getEncryptPath(destPath);
        }

        Logger.i("Using %s for dest path", destPath);

        BlackHole.eat(new File(destPath).delete());

        Files.createParentDirs(new File(destPath));

        OutputStream outputStream = Files.asByteSink(new File(destPath)).openStream();

        int total = 0;
        byte[] buffer = createBuffer();
        while (total < size) {
            int result = in.read(buffer);
            outputStream.write(buffer, 0, result);
            if (result == -1) {
                break;
            }
            total += result;
        }

        Acknowledge.okTo(os);

        return OK;
    }

}
