package org.newstand.datamigration.net;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataCategory;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/22 13:05
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@Setter
public class ReceiveSettings {
    private String rootDir;
    private DataCategory category;
    private Consumer<String> nameConsumer;
}
