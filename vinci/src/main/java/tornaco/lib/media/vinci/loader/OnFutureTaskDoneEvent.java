package tornaco.lib.media.vinci.loader;

import android.graphics.Bitmap;

import java.util.concurrent.FutureTask;

import lombok.Getter;
import lombok.ToString;

/**
 * Created by Nick on 2017/5/5 22:06
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */
@Getter
@ToString
public class OnFutureTaskDoneEvent {
    private String sourceUrl;
    private FutureTask<Bitmap> task;
    private String imageConsumerId;

    OnFutureTaskDoneEvent(String sourceUrl, FutureTask<Bitmap> task, String imageConsumerId) {
        this.sourceUrl = sourceUrl;
        this.task = task;
        this.imageConsumerId = imageConsumerId;
    }
}
