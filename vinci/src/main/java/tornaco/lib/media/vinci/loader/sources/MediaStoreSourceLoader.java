package tornaco.lib.media.vinci.loader.sources;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Nick on 2017/5/5 17:18
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

class MediaStoreSourceLoader extends FileSourceLoader {

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

            Source fileSource = new Source();
            fileSource.setSourceUrl(filePath);
            fileSource.setContext(source.getContext());

            return super.loadFromSource(fileSource);
        } finally {
            cursor.close();
        }
    }

    @Override
    public boolean canHandle(@NonNull String sourceUrl) {
        return sourceUrl.startsWith(sourceUrlPrefix());
    }

    private String sourceUrlPrefix() {
        return "content://";
    }
}
