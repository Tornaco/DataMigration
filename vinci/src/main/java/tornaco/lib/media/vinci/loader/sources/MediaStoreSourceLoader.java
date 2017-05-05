package tornaco.lib.media.vinci.loader.sources;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Nick on 2017/5/5 17:18
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public class MediaStoreSourceLoader implements SourceLoader {
    @Nullable
    @Override
    public Bitmap loadFromSource(@NonNull Source source) {

        Uri uri = Uri.parse(source.getSourceUrl());

        String[] pro = {MediaStore.Images.Media.DATA};

        Cursor cursor = source.getContext().getContentResolver().query(uri, pro, null, null, null);

        if (cursor == null) {
            return null;
        }

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            if (index < 0) {
                return null;
            }

            cursor.moveToFirst();

            String filePath = cursor.getString(index);

            return BitmapFactory.decodeFile(filePath);
        } finally {
            cursor.close();
        }
    }

    @Override
    public String sourceUrlPrefix() {
        return "content://";
    }
}
