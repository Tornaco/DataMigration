package org.newstand.datamigration.data.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Nick on 2017/6/21 13:07
 */

@Getter
@Setter
@NoArgsConstructor
public class SettingsRecord extends FileBasedRecord {
    private String key;
    private String value;
    private String namespace;
}
