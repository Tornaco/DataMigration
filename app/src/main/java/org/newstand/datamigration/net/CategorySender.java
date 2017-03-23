package org.newstand.datamigration.net;

import org.newstand.datamigration.net.protocol.CategoryHeader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Nick@NewStand.org on 2017/3/22 19:45
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class CategorySender extends AbsSender<CategoryHeader> {

    private InputStream inputStream;
    private OutputStream outputStream;

    private CategorySender(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public static CategorySender with(InputStream inputStream, OutputStream outputStream) {
        return new CategorySender(inputStream, outputStream);
    }

    @Override
    public int send(CategoryHeader categoryHeader) throws IOException {
        categoryHeader.writeTo(outputStream);
        return waitForAck(inputStream);
    }
}
