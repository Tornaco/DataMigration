package dev.tornaco.vangogh.request;

import org.newstand.logger.Logger;

import dev.tornaco.vangogh.media.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Delegate;

/**
 * Created by guohao4 on 2017/8/25.
 * Email: Tornaco@163.com
 */
@AllArgsConstructor
@Getter
@Setter
@ToString
public class DisplayRequest implements Runnable {
    private Image image;
    @Delegate
    private ImageRequest imageRequest;
    private Object arg;

    @Override
    public void run() {
        Logger.v("DisplayRequest, run, arg: %s", arg);
        if (imageRequest.getApplier() != null && arg == null) {
            imageRequest.getApplier().apply(imageRequest.getDisplayer(), image);
        } else {
            imageRequest.getDisplayer().display(image);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DisplayRequest that = (DisplayRequest) o;

        return imageRequest.equals(that.imageRequest);

    }

    @Override
    public int hashCode() {
        return imageRequest.hashCode();
    }
}
