package org.newstand.datamigration.data.model;

import com.google.common.io.Files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/7 13:24
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class FileBasedRecord extends DataRecord {
    private long size;
    private String path;

    public long calculateSize() throws IOException {
        if (path == null || !new File(path).exists()) throw new FileNotFoundException(path);
        return size == 0 ? Files.asByteSource(new File(path)).size() : size;
    }
}
