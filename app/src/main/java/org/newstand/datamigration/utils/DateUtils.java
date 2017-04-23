package org.newstand.datamigration.utils;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Nick@NewStand.org on 2017/3/26 17:00
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DateUtils {

    public static String formatLong(long l) {
        String time;
        DateFormat format = DateFormat.getDateInstance(DateFormat.FULL);
        Date d1 = new Date(l);
        time = format.format(d1);
        DateFormat timeInstance = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);
        return time + "\t" + timeInstance.format(d1);
    }
}
