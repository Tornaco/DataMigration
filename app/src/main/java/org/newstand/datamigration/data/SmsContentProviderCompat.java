package org.newstand.datamigration.data;

import android.net.Uri;

/**
 * Created by Nick@NewStand.org on 2017/3/13 13:36
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SmsContentProviderCompat {

    public static final Uri INBOX_CONTENT_URI = Uri.parse("content://sms/inbox");
    public static final Uri SENT_CONTENT_URI = Uri.parse("content://sms/sent");
    public static final Uri DRAFT_CONTENT_URI = Uri.parse("content://sms/draft");
}
