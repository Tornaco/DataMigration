package org.newstand.datamigration.data.model;

import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/7 13:24
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@NoArgsConstructor
@ToString(callSuper = true)
public class PhotoRecord extends FileBasedRecord {
    @Override
    public DataCategory category() {
        return DataCategory.Photo;
    }
}
