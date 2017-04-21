package org.newstand.datamigration.net.protocol;

import com.google.common.base.Optional;
import com.google.common.io.Files;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import org.newstand.datamigration.provider.SettingsProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.Getter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/22 10:29
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@ToString
public class FileHeader implements Serializable, DeSerializable, ByteWriter {

    @Getter
    private String fileName;
    @Getter
    private long size;

    private FileHeader(String fileName, long size) {
        this.fileName = Optional.fromNullable(fileName).or("Unknown name");// FIXME Does this happen?
        this.size = size;
    }

    public static FileHeader from(String path, String fileName) throws IOException {
        long fileSize = Files.asByteSource(new File(path)).size();
        boolean isEncrypted = SettingsProvider.isEncryptedFile(path);
        return new FileHeader(isEncrypted ? SettingsProvider.getEncryptedName(fileName) : fileName, fileSize);
    }

    public static FileHeader from(byte[] data) {
        FileHeader h = new FileHeader(null, 0);
        h.inflateWithBytes(data);
        return h;
    }

    public static FileHeader from(InputStream in) throws IOException {
        byte[] lengthBytes = new byte[Ints.BYTES];
        int ret = in.read(lengthBytes);
        if (ret == -1) {
            throw new IOException("Bad ret in read");
        }
        int length = Ints.fromByteArray(lengthBytes);
        byte[] nameBytes = new byte[length];
        ret = in.read(nameBytes);
        if (ret == -1) {
            throw new IOException("Bad ret in read");
        }
        String name = new String(nameBytes);

        byte[] sizeBytes = new byte[Longs.BYTES];
        ret = in.read(sizeBytes);
        if (ret == -1) {
            throw new IOException("Bad ret in read");
        }
        long size = Longs.fromByteArray(sizeBytes);
        return new FileHeader(name, size);
    }

    @Override
    public void inflateWithBytes(byte[] data) {
        byte[] lengthBytes = new byte[Ints.BYTES];
        System.arraycopy(data, 0, lengthBytes, 0, Ints.BYTES);
        int length = Ints.fromByteArray(lengthBytes);
        byte[] nameBytes = new byte[length];
        System.arraycopy(data, Ints.BYTES, nameBytes, 0, nameBytes.length);
        fileName = new String(nameBytes);

        byte[] sizeBytes = new byte[Longs.BYTES];
        System.arraycopy(data, Ints.BYTES + length, sizeBytes, 0, sizeBytes.length);
        size = Longs.fromByteArray(sizeBytes);
    }

    @Override
    public byte[] toBytes() {
        byte[] content = fileName.getBytes();
        int length = content.length;
        return Bytes.concat(Ints.toByteArray(length), content, Longs.toByteArray(size));
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        os.write(toBytes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileHeader header = (FileHeader) o;

        return fileName != null ? fileName.equals(header.fileName) : header.fileName == null;

    }

    @Override
    public int hashCode() {
        return fileName != null ? fileName.hashCode() : 0;
    }
}
