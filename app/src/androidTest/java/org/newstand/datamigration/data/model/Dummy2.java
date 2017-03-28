package org.newstand.datamigration.data.model;

import io.realm.RealmModel;
import io.realm.annotations.RealmClass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/27 17:01
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RealmClass
public class Dummy2 implements RealmModel{
    private String dir;
    private boolean checked;

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
