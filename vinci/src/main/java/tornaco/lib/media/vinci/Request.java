package tornaco.lib.media.vinci;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lombok.Getter;
import lombok.ToString;
import tornaco.lib.media.vinci.display.ImageConsumer;
import tornaco.lib.media.vinci.display.ImageViewImageConsumer;
import tornaco.lib.media.vinci.effect.Animator;
import tornaco.lib.media.vinci.effect.EffectProcessor;
import tornaco.lib.media.vinci.loader.Loader;
import tornaco.lib.media.vinci.loader.RequestExecutor;

/**
 * Created by Nick on 2017/5/5 11:17
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */
@ToString
@Getter
public final class Request {

    private String sourceUrl;

    private List<ImageConsumer> imageConsumers;

    private List<Loader> loaders;

    private EffectProcessor effectProcessor;

    private Animator animator;

    private Context context;

    private
    @DrawableRes
    int placeHolderRes;

    private
    @DrawableRes
    int errorRes;

    Request(Context context, String sourceUrl, List<ImageConsumer> initialConsumers) {
        this.sourceUrl = sourceUrl;
        this.loaders = new ArrayList<>();
        this.imageConsumers = initialConsumers;
        this.context = context;
    }

    public void into(@NonNull ImageView imageView) {
        into(new ImageViewImageConsumer(imageView));
    }

    public void into(@NonNull ImageConsumer imageConsumer) {
        Enforcer.enforce(!this.imageConsumers.contains(Enforcer.enforceNonNull(imageConsumer)), "Duplicate consumer.");

        this.imageConsumers.add(imageConsumer);

        // Let's to to work.
        new RequestExecutor(this).execute();
    }

    public Request placeHolder(@DrawableRes int drawableRes) {
        this.placeHolderRes = drawableRes;
        return this;
    }

    public Request error(@DrawableRes int drawableRes) {
        this.errorRes = drawableRes;
        return this;
    }

    public Request processor(@NonNull EffectProcessor effectProcessor) {
        this.effectProcessor = effectProcessor;
        return this;
    }

    public Request animator(@NonNull Animator animator) {
        this.animator = animator;
        return this;
    }

    public Request loader(@NonNull Loader loader) {
        Enforcer.enforce(!loaders.contains(loader), "Duplicate loader is not allowed.");
        this.loaders.add(Enforcer.enforceNonNull(loader));
        Collections.sort(loaders, new Comparator<Loader>() {
            @Override
            public int compare(Loader o1, Loader o2) {
                if (o1.priority() > o2.priority()) return 1;
                if (o1.priority() < o2.priority()) return -1;
                return 0;
            }
        });
        return this;
    }
}
