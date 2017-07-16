package org.newstand.datamigration.data.model;

import android.net.Uri;

import java.io.IOException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/7 10:57
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class ContactRecord extends FileBasedRecord {
    private String email;
    private String phoneNum;
    private Uri uri;

    @Override
    public DataCategory category() {
        return DataCategory.Contact;
    }

    @Override
    public long calculateSize() throws IOException {
        if (getPath() == null) {
            // A contact backup file is estimated to 512kb.
            return 512 * 1024;
        }
        return super.calculateSize();
    }
}
