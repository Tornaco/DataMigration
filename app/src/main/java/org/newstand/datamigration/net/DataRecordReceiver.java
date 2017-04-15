package org.newstand.datamigration.net;

import com.google.common.io.Files;
import com.google.common.primitives.Ints;

import org.newstand.datamigration.net.protocol.ACK;
import org.newstand.datamigration.net.protocol.FileHeader;
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

        Logger.d("Receiving: %s", fileHeader.toString());

        ACK.okTo(os);

        String fileName = fileHeader.getFileName();

        // Notify the name of this file.
        settings.getNameConsumer().accept(fileName);

        long size = fileHeader.getSize();

        String destPath = settings.getDestDir() + File.separator + fileName;

        int sizeInt = Ints.checkedCast(size);// FIXME

        BlackHole.eat(new File(destPath).delete());

        Files.createParentDirs(new File(destPath));

        OutputStream outputStream = Files.asByteSink(new File(destPath)).openStream();

        int total = 0;
        byte[] buffer = createBuffer();
        while (total < sizeInt) {
            int result = in.read(buffer);
            outputStream.write(buffer, 0, result);
            if (result == -1) {
                break;
            }
            total += result;
        }

        ACK.okTo(os);

        return OK;
    }

}
