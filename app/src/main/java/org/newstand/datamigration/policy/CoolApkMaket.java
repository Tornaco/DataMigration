package org.newstand.datamigration.policy;

import android.content.Context;
import android.net.Uri;

import org.codechimp.apprater.Market;

/**
 * Created by Nick@NewStand.org on 2017/5/3 13:22
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class CoolApkMaket implements Market {
    @Override
    public Uri getMarketURI(Context context) {
        return Uri.parse("http://www.coolapk.com/apk/org.newstand.datamigration");
    }
}
