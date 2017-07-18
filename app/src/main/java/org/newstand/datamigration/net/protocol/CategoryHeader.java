package org.newstand.datamigration.net.protocol;

import android.support.annotation.NonNull;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.data.model.FileBasedRecord;
import org.newstand.datamigration.utils.Collections;
import org.newstand.logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;

import lombok.Getter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/21 14:23
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@ToString
public class CategoryHeader implements Serializable, DeSerializable, ByteWriter {

    private static final int BYTES = 2 * Ints.BYTES + Longs.BYTES;

    @Getter
    private DataCategory dataCategory;
    @Getter
    private int fileCount;
    @Getter
    private long fileSize;

    private CategoryHeader(DataCategory dataCategory) {
        this.dataCategory = dataCategory;
    }

    public static CategoryHeader empty() {
        return new CategoryHeader(null);
    }

    public static CategoryHeader from(byte[] data) {
        CategoryHeader h = empty();
        h.inflateWithBytes(data);
        return h;
    }

    public static CategoryHeader from(DataCategory dataCategory) {
        return new CategoryHeader(dataCategory);
    }

    public static CategoryHeader from(InputStream inputStream) throws IOException {
        byte[] src = new byte[BYTES];
        int ret = inputStream.read(src);
        if (ret != BYTES) {
            throw new IOException("Fail to read @need FIX");
        }
        return from(src);
    }

    public CategoryHeader add(Collection<DataRecord> recordCollections) {
        if (Collections.isNullOrEmpty(recordCollections)) return this;

        Collections.consumeRemaining(recordCollections, new Consumer<DataRecord>() {
            @Override
            public void accept(@NonNull DataRecord dataRecord) {
                FileBasedRecord fb = (FileBasedRecord) dataRecord;
                try {
                    long size = fb.calculateSize();
                    fileSize += size;
                } catch (IOException e) {
                    Logger.e(e, "Fail to get file size");
                }

                fileCount++;
            }
        });
        return this;
    }

    @Override
    public void inflateWithBytes(byte[] data) {
        byte[] countBytes = new byte[Ints.BYTES];
        System.arraycopy(data, 0, countBytes, 0, Ints.BYTES);
        fileCount = Ints.fromByteArray(countBytes);

        byte[] sizeBytes = new byte[Longs.BYTES];
        System.arraycopy(data, Ints.BYTES, sizeBytes, 0, Longs.BYTES);
        fileSize = Longs.fromByteArray(sizeBytes);

        byte[] categories = new byte[Ints.BYTES];
        System.arraycopy(data, Ints.BYTES + Longs.BYTES, categories, 0, categories.length);
        dataCategory = DataCategory.fromInt(Ints.fromByteArray(categories));
        Logger.d("CategoryHeader:inflateWithBytes:%s", dataCategory);

        if (dataCategory == null) {
            Logger.e("Error parsing CategoryHeade:%s", Arrays.toString(data));
        }
    }

    @Override
    public byte[] toBytes() {
        return Bytes.concat(Ints.toByteArray(fileCount), Longs.toByteArray(fileSize), Ints.toByteArray(dataCategory.ordinal()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryHeader header = (CategoryHeader) o;

        if (fileCount != header.fileCount) return false;
        if (fileSize != header.fileSize) return false;
        return dataCategory == header.dataCategory;

    }

    @Override
    public int hashCode() {
        int result = dataCategory != null ? dataCategory.hashCode() : 0;
        result = 31 * result + fileCount;
        result = 31 * result + (int) (fileSize ^ (fileSize >>> 32));
        return result;
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        os.write(toBytes());
    }
}
