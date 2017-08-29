package dev.tornaco.vangogh.media;

import android.support.annotation.DrawableRes;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by guohao4 on 2017/8/24.
 * Email: Tornaco@163.com
 */
@ToString
@Getter
@Setter
public class ImageSource implements Cloneable {
    private String url;
    @DrawableRes
    private int placeHolder;
    @DrawableRes
    private int fallback;

    private boolean skipDiskCache;
    private boolean skipMemoryCache;

    public ImageSource duplicate() throws CloneNotSupportedException {
        return (ImageSource) clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageSource source = (ImageSource) o;

        return url.equals(source.url);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    public enum SourceType {

        File("file://"),
        Content("content://"),
        Http("http://"),
        Https("https://"),
        Drawable("drawable://"),
        Mipmap("mipmap://"),
        Assets("assets://");

        public String prefix;

        SourceType(String prefix) {
            this.prefix = prefix;
        }
    }

}
