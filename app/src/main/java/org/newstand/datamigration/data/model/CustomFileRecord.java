package org.newstand.datamigration.data.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/31 10:52
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

@NoArgsConstructor
@ToString(callSuper = true)
@Setter
@Getter
public class CustomFileRecord extends FileBasedRecord {
    private boolean isDir;

    @Override
    public DataCategory category() {
        return DataCategory.CustomFile;
    }
}
