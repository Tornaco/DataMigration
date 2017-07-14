package org.newstand.datamigration.data.model;

import android.widget.Checkable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/7 10:00
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
public class DataRecord implements Checkable {

    private String displayName;
    private String id;
    private boolean isChecked;

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }

    public DataCategory category() {
        throw new UnsupportedOperationException("This method has not been override!");
    }
}
