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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/21 14:06
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@ToString()
public class OverviewHeader implements Serializable, DeSerializable, ByteWriter {

    @Getter
    private Set<DataCategory> dataCategories;

    @Getter
    private int fileCount;
    @Getter
    private long fileSize;


    private OverviewHeader() {
        dataCategories = new HashSet<>();
    }

    public static OverviewHeader empty() {
        return new OverviewHeader();
    }

    public static OverviewHeader from(byte[] data) {
        OverviewHeader h = empty();
        h.inflateWithBytes(data);
        return h;
    }

    public static OverviewHeader from(InputStream inputStream) throws IOException {
        OverviewHeader h = empty();

        byte[] firstBatch = new byte[2 * Ints.BYTES + Longs.BYTES];

        int c = inputStream.read(firstBatch);

        if (c != firstBatch.length) throw new IOException("Failed to read @need FIX");

        byte[] countBytes = new byte[Ints.BYTES];
        System.arraycopy(firstBatch, 0, countBytes, 0, Ints.BYTES);
        h.fileCount = Ints.fromByteArray(countBytes);

        byte[] sizeBytes = new byte[Longs.BYTES];
        System.arraycopy(firstBatch, Ints.BYTES, sizeBytes, 0, Longs.BYTES);
        h.fileSize = Longs.fromByteArray(sizeBytes);

        byte[] ccntBytes = new byte[Ints.BYTES];
        System.arraycopy(firstBatch, Ints.BYTES + Longs.BYTES, ccntBytes, 0, ccntBytes.length);
        int cct = Ints.fromByteArray(ccntBytes);

        byte[] secondBatch = new byte[cct * Ints.BYTES];

        c = inputStream.read(secondBatch);
        if (c != secondBatch.length) throw new IOException("Failed to read @need FIX");

        for (int i = 0; i < cct; i++) {
            byte[] categoryBytes = new byte[Ints.BYTES];
            System.arraycopy(secondBatch, i * Ints.BYTES, categoryBytes, 0, Ints.BYTES);
            DataCategory dc = DataCategory.fromInt(Ints.fromByteArray(categoryBytes));
            h.dataCategories.add(dc);
        }

        return h;
    }

    public static OverviewHeader from(DataCategory category, Collection<DataRecord> recordCollections) {
        return empty().add(category, recordCollections);
    }

    public OverviewHeader add(DataCategory category, Collection<DataRecord> recordCollections) {
        if (Collections.isNullOrEmpty(recordCollections)) return this;

        if (!dataCategories.contains(category)) {
            dataCategories.add(category);
        }

        Collections.consumeRemaining(recordCollections, new Consumer<DataRecord>() {
            @Override
            public void accept(@NonNull DataRecord dataRecord) {
                FileBasedRecord fb = (FileBasedRecord) dataRecord;

                long size = 0;
                try {
                    size = fb.calculateSize();
                } catch (IOException e) {
                    Logger.e(e, "Fail calculate size:" + fb);
                }
                fileSize += size;

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

        byte[] ccntBytes = new byte[Ints.BYTES];
        System.arraycopy(data, Ints.BYTES + Longs.BYTES, ccntBytes, 0, ccntBytes.length);
        int cct = Ints.fromByteArray(ccntBytes);

        if (data.length == ccntBytes.length + sizeBytes.length + countBytes.length) return;

        byte[] categories = new byte[cct * Ints.BYTES];
        System.arraycopy(data, 2 * Ints.BYTES + Longs.BYTES, categories, 0, categories.length);

        for (int i = 0; i < cct; i++) {
            byte[] categoryBytes = new byte[Ints.BYTES];
            System.arraycopy(categories, i * Ints.BYTES, categoryBytes, 0, Ints.BYTES);
            DataCategory c = DataCategory.fromInt(Ints.fromByteArray(categoryBytes));
            dataCategories.add(c);
        }
    }

    @Override
    public byte[] toBytes() {

        final byte[][] categories = {new byte[0]};

        Collections.consumeRemaining(dataCategories, new Consumer<DataCategory>() {
            @Override
            public void accept(@NonNull DataCategory category) {
                int ord = category.ordinal();
                categories[0] = Bytes.concat(categories[0], Ints.toByteArray(ord));
            }
        });

        return Bytes.concat(Ints.toByteArray(fileCount), Longs.toByteArray(fileSize), Ints.toByteArray(dataCategories.size()), categories[0]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OverviewHeader header = (OverviewHeader) o;

        if (fileCount != header.fileCount) return false;
        if (fileSize != header.fileSize) return false;
        return dataCategories != null ? dataCategories.equals(header.dataCategories) : header.dataCategories == null;

    }

    @Override
    public int hashCode() {
        int result = dataCategories != null ? dataCategories.hashCode() : 0;
        result = 31 * result + fileCount;
        result = 31 * result + (int) (fileSize ^ (fileSize >>> 32));
        return result;
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        os.write(toBytes());
    }
}
