package org.newstand.datamigration.data;

/**
 * Created by Nick@NewStand.org on 2017/4/19 13:39
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class CallLogCompat {
    /**
     * Call log type for incoming calls.
     */
    public static final int INCOMING_TYPE = 1;
    /**
     * Call log type for outgoing calls.
     */
    public static final int OUTGOING_TYPE = 2;
    /**
     * Call log type for missed calls.
     */
    public static final int MISSED_TYPE = 3;
    /**
     * Call log type for voicemails.
     */
    public static final int VOICEMAIL_TYPE = 4;
    /**
     * Call log type for calls rejected by direct user action.
     */
    public static final int REJECTED_TYPE = 5;
    /**
     * Call log type for calls blocked automatically.
     */
    public static final int BLOCKED_TYPE = 6;
    /**
     * Call log type for a call which was answered on another device.  Used in situations where
     * a call rings on multiple devices simultaneously and it ended up being answered on a
     * device other than the current one.
     */
    public static final int ANSWERED_EXTERNALLY_TYPE = 7;
}
