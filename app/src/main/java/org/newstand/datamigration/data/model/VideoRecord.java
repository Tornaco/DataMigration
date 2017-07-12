package org.newstand.datamigration.data.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/7 13:24
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@NoArgsConstructor
@ToString(callSuper = true)
public class VideoRecord extends FileBasedRecord {

    @Getter
    @Setter
    private long duration;

    @Override
    public DataCategory category() {
        return DataCategory.Video;
    }
}
