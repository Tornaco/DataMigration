package org.newstand.datamigration.data.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/7 12:52
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class MusicRecord extends FileBasedRecord {

    private String artist;
    private long duration;
    private String artUri;

    @Override
    public DataCategory category() {
        return DataCategory.Music;
    }
}
