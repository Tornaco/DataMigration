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
public class OnFutureTaskCommitEvent {
    private FutureTask<Bitmap> task;
    private String sourceUrl;
    private String imageConsumerId;

    OnFutureTaskCommitEvent(FutureTask<Bitmap> task, String sourceUrl, String imageConsumerId) {
        this.task = task;
        this.sourceUrl = sourceUrl;
        this.imageConsumerId = imageConsumerId;
    }
}
