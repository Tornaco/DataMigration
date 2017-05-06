package tornaco.lib.media.vinci.loader.sources;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

import tornaco.lib.media.vinci.utils.BitmapUtils;
import tornaco.lib.media.vinci.utils.Logger;

/**
 * Created by Nick on 2017/5/6 12:28
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

class FileSourceLoader implements SourceLoader {

    protected static final int UNCONSTRAINED = -1;

    /* Maximum pixels size for created bitmap. */
    protected static final int MAX_NUM_PIXELS_THUMBNAIL = 512 * 512;

    /*
    * Compute the sample size as a function of minSideLength
    * and maxNumOfPixels.
    * minSideLength is used to specify that minimal width or height of a
    * bitmap.
    * maxNumOfPixels is used to specify the maximal size in pixels that is
    * tolerable in terms of memory usage.
    *
    * The function returns a sample size based on the constraints.
    * Both size and minSideLength can be passed in as IImage.UNCONSTRAINED,
    * which indicates no care of the corresponding constraint.
    * The functions prefers returning a sample size that
    * generates a smaller bitmap, unless minSideLength = IImage.UNCONSTRAINED.
    *
    * Also, the function rounds up the sample size to a power of 2 or multiple
    * of 8 because BitmapFactory only honors sample size this way.
    * For example, BitmapFactory downsamples an image by 2 even though the
    * request is 3. So we round up the sample size to avoid OOM.
    */
    protected int computeSampleSize(BitmapFactory.Options options,
                                    int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private int computeInitialSampleSize(BitmapFactory.Options options,
                                         int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == UNCONSTRAINED) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == UNCONSTRAINED) &&
                (minSideLength == UNCONSTRAINED)) {
            return 1;
        } else if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    @Nullable
    @Override
    public Bitmap loadFromSource(@NonNull Source source) {
        String path = source.getSourceUrl();
        File file = new File(path);

        if (!file.exists()) return null;

        BitmapFactory.Options decodeOptions;

        decodeOptions = new BitmapFactory.Options();
        // If we have to resize this image, first get the natural bounds.
        decodeOptions.inJustDecodeBounds = true;
        BitmapUtils.decodeFileLocked(path, decodeOptions);

        // Decode to the nearest power of two scaling factor.
        decodeOptions.inJustDecodeBounds = false;
        decodeOptions.inSampleSize =
                computeSampleSize(decodeOptions, UNCONSTRAINED, MAX_NUM_PIXELS_THUMBNAIL);

        Bitmap bitmap;

        try {
            bitmap = BitmapUtils.decodeFileLocked(path, decodeOptions);
        } catch (Throwable error) {
            Logger.d("Error!!! BitmapFactory.decodeFile %s", Logger.getStackTraceString(error));
            return null;
        }


        return bitmap;
    }

    @Override
    public boolean canHandle(@NonNull String sourceUrl) {
        return sourceUrl.startsWith("/storage");
    }
}
