package org.newstand.datamigration.ui.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.newstand.datamigration.R;

import dev.tornaco.vangogh.Vangogh;

/**
 * Created by guohao4 on 2017/7/7.
 */

public class ImageViewerDialog {

    public static void attach(Context context, String titleStr, String path) {
        View layout = LayoutInflater.from(context).inflate(R.layout.layout_image_viewer, null);
        ImageView imageView = (ImageView) layout.findViewById(R.id.image);
        TextView textView = (TextView) layout.findViewById(android.R.id.text1);
        MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title(titleStr)
//                .titleColorAttr(R.attr.colorAccent)
                .customView(layout, true)
                .positiveColorAttr(R.attr.colorAccent)
                .positiveText(android.R.string.ok)
                .cancelable(true)
                .autoDismiss(true)
                .canceledOnTouchOutside(true)
                .build();
        materialDialog.show();
        Vangogh.with(context).load(path)
                .fallback(R.drawable.ic_media_empty)
                .into(imageView);
        textView.setText(context.getString(R.string.details_images_path, path));
    }
}
