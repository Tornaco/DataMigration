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

        StringBuilder sb = new StringBuilder("\n\n");
        sb.append("network={")
                .append("\n");

        for (String line : rawLines) {
            sb.append("\t").append(line).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
