package org.newstand.datamigration.common;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/4/15 13:39
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@Setter
public class Holder<T> {
    private T data;
}
