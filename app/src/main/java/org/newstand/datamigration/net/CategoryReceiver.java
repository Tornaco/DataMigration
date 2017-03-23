package org.newstand.datamigration.net;

import com.orhanobut.logger.Logger;

import org.newstand.datamigration.net.protocol.ACK;
import org.newstand.datamigration.net.protocol.CategoryHeader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/22 19:39
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class CategoryReceiver implements Receiver<Void> {

    @Getter
    private InputStream inputStream;
    @Getter
    private OutputStream outputStream;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private CategoryHeader header;

    private CategoryReceiver(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public static CategoryReceiver with(InputStream inputStream, OutputStream outputStream) {
        return new CategoryReceiver(inputStream, outputStream);
    }

    @Override
    public int receive(Void v) throws IOException {

        CategoryHeader header = CategoryHeader.from(inputStream);

        Logger.i("received header: %s", header);

        ACK.okTo(outputStream);

        setHeader(header);

        return OK;
    }
}
