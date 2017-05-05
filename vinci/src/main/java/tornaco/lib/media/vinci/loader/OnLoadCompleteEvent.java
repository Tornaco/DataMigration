package tornaco.lib.media.vinci.loader;

import android.graphics.Bitmap;

import lombok.Getter;
import lombok.ToString;

/**
 * Created by Nick on 2017/5/5 16:09
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */
@Getter
@ToString
public class OnLoadCompleteEvent {

    private Loader who;
    private String sourceUrl;
    private Bitmap image;

    public OnLoadCompleteEvent(Loader who, String sourceUrl, Bitmap image) {
        this.who = who;
        this.sourceUrl = sourceUrl;
        this.image = image;
    }
}
