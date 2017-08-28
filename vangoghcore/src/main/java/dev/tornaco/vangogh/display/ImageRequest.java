package dev.tornaco.vangogh.display;

import dev.tornaco.vangogh.media.ImageSource;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by guohao4 on 2017/8/24.
 * Email: Tornaco@163.com
 */
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class ImageRequest {
    private int id;
    private String alias;
    private long requestTimeMills;
    private ImageSource imageSource;
    private ImageDisplayer displayer;
    private ImageApplier applier;
    private ImageEffect[] effect;

    private boolean dirty;

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageRequest that = (ImageRequest) o;

        return displayer.equals(that.displayer);

    }

    @Override
    public int hashCode() {
        return displayer.hashCode();
    }
}
