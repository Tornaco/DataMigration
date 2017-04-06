package org.newstand.datamigration.utils;

import android.icu.text.SimpleDateFormat;

import java.sql.Date;

/**
 * Created by Nick@NewStand.org on 2017/3/26 17:00
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DateUtils {

    public static String formatLong(long l) {
        String time;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
            Date d1 = new Date(l);
            time = format.format(d1);
        } else {
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
            Date d1 = new Date(l);
            time = format.format(d1);
        }
        return time;
    }
}
