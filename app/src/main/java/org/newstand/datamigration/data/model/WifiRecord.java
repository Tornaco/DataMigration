package org.newstand.datamigration.data.model;

import java.util.ArrayList;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/4/25 15:39
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

@Getter
@Setter
@NoArgsConstructor
public class WifiRecord extends FileBasedRecord {
    private String ssid;
    private String psk;

    private ArrayList<String> rawLines;

    @Override
    public DataCategory category() {
        return DataCategory.Wifi;
    }

    @Override
    public String toString() {
        String d = "network={\n";
        for (String line : rawLines) {
            d = d + String.format("   %s\n", line);
        }
        d = d + "}";
        return d;
    }
}
