package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;

import org.newstand.datamigration.R;
import org.newstand.datamigration.provider.SettingsProvider;

import dev.nick.tiles.tile.QuickTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/6 18:26
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DonateTile extends ThemedTile {

    public DonateTile(@NonNull Context context) {
        super(context, null);
    }

    @Override
    void onInitView(Context context) {
        this.titleRes = R.string.title_donate;
        this.iconRes = R.drawable.ic_coffee;

        this.tileView = new QuickTileView(getContext(), this) {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                showXCode();
            }
        };
    }

    private void showXCode() {
        ImageView imageView = new ImageView(getContext());
        String donateFilePath = SettingsProvider.getDonateQrPathChecked();
        Bitmap bitmap = donateFilePath != null ? BitmapFactory.decodeFile(donateFilePath)
                : BitmapFactory.decodeResource(getContext().getResources(), R.drawable.qr);
        imageView.setImageBitmap(bitmap);
        new AlertDialog.Builder(getContext())
                .setView(imageView)
                .setTitle(R.string.title_donate)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nothing.
                    }
                })
                .create()
                .show();
    }
}
