package org.newstand.datamigration.utils;

import android.os.Build;
import androidx.annotation.RequiresApi;

/**
 * Created by Nick@NewStand.org on 2017/5/2 11:13
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class EmojiUtils {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }
}
