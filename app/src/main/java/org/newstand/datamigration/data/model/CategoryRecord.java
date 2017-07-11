package org.newstand.datamigration.data.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/7 18:29
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

@Setter
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
public class CategoryRecord extends DataRecord {
    private DataCategory category;
    private String summary;

    @Override
    public DataCategory category() {
        return category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryRecord that = (CategoryRecord) o;
        return category == that.category;
    }

    @Override
    public int hashCode() {
        return category.hashCode();
    }
}