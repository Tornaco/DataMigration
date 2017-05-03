package org.newstand.datamigration.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;

import com.nononsenseapps.filepicker.FilePickerActivity;

/**
 * Created by Nick@NewStand.org on 2017/5/3 10:19
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class FilePickerUtils {

    public static void pickSingleDir(Activity activity, int code) {
        // This always works
        Intent i = new Intent(activity, FilePickerActivity.class);
        // This works if you defined the intent filter
        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

        // Set these depending on your use case. These are the defaults.
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);

        // Configure initial directory by specifying a String.
        // You could specify a String like "/storage/emulated/0/", but that can
        // dangerous. Always use Android's API calls to get paths to the SD-card or
        // internal memory.
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        activity.startActivityForResult(i, code);
    }
}
