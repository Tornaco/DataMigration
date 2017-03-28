package org.newstand.datamigration.data.model;

import io.realm.RealmModel;
import io.realm.annotations.RealmClass;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/27 16:50
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@Setter
@RealmClass
public class Dummy implements RealmModel {
    private int id;

    private String name;
}
