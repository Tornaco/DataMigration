package tornaco.lib.media.vinci;

import android.content.Context;
import android.support.annotation.NonNull;

import tornaco.lib.media.vinci.utils.Logger;

/**
 * Created by Nick on 2017/2/10 11:15
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public class Vinci {

    private static Vinci sOnlyOne;

    /**
     * @return Single instance of {@link Vinci}.
     */
    public synchronized static Vinci config(VinciConfig config) {
        Enforcer.enforce(sOnlyOne == null, "Duplicate config is not allowed.");
        sOnlyOne = new Vinci(config);
        return sOnlyOne;
    }

    private static Vinci enforce() {
        Enforcer.enforce(sOnlyOne != null, "Please config Vinci first!");
        return sOnlyOne;
    }

    private Vinci(VinciConfig config) {
        RequestFactory.init(config);
        Logger.dbg("Init da vinci with %s", config);
    }

    /**
     * @param context Application context is preferred.
     * @return {@link Request} instance.
     */
    public
    @NonNull
    static Request load(Context context,
                        @NonNull String sourceUri) {
        enforce();
        return RequestFactory.newRequest(context.getApplicationContext(), Enforcer.enforceNonNull(sourceUri));
    }
}
