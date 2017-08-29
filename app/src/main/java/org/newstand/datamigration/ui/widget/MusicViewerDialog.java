package org.newstand.datamigration.ui.widget;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nick.yinheng.model.IMediaTrack;
import com.nick.yinheng.service.IMusicPlayerService;

import org.newstand.datamigration.R;
import org.newstand.datamigration.service.MediaPlayerService;
import org.newstand.logger.Logger;

import dev.tornaco.vangogh.Vangogh;
import dev.tornaco.vangogh.loader.LoaderObserverAdapter;
import dev.tornaco.vangogh.media.Image;
import lombok.Getter;

/**
 * Created by guohao4 on 2017/7/7.
 */
public class MusicViewerDialog {

    @Getter
    private Context context;

    private IMusicPlayerService playerService;

    private FloatingActionButton fab;

    public MusicViewerDialog(Context context) {
        this.context = context;
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playerService = IMusicPlayerService.Stub.asInterface(service);
            fab.show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void bindPlayer() {
        context.bindService(MediaPlayerService.Proxy.getIntent(context), connection, Context.BIND_AUTO_CREATE);
    }

    private void unBind() {
        if (playerService != null) {
            try {
                playerService.stop();
            } catch (RemoteException ignored) {

            }
            context.unbindService(connection);
        }
    }

    public void attach(String titleStr, final String path, String artUrl) {
        View layout = LayoutInflater.from(context).inflate(R.layout.layout_music_viewer, null);
        ImageView imageView = (ImageView) layout.findViewById(R.id.image);
        TextView textView = (TextView) layout.findViewById(android.R.id.text1);
        this.fab = (FloatingActionButton) layout.findViewById(R.id.fab);
        this.fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMediaTrack track = new IMediaTrack();
                track.setUrl(path);

                try {
                    if (playerService != null) {
                        if (playerService.isPlaying()) {
                            playerService.stop();
                            fab.setImageResource(R.drawable.ic_play);
                        } else {
                            playerService.play(track);
                            fab.setImageResource(R.drawable.ic_stop);
                        }
                    }
                } catch (Throwable e) {
                    Logger.e(e, "Fail playing");
                }
            }
        });
        MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title(titleStr)
//                .titleColorAttr(R.attr.colorAccent)
                .customView(layout, true)
                .positiveColorAttr(R.attr.colorAccent)
                .positiveText(android.R.string.ok)
                .cancelable(true)
                .autoDismiss(true)
                .canceledOnTouchOutside(true)
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        unBind();
                    }
                })
                .build();
        materialDialog.show();
        Vangogh.with(context).load(artUrl)
                .fallback(R.drawable.ic_media_empty)
                .observer(new LoaderObserverAdapter() {
                    @Override
                    public void onImageReady(@NonNull Image image) {
                        super.onImageReady(image);
                        if (playerService != null) fab.show();
                    }
                })
                .into(imageView);
        textView.setText(context.getString(R.string.details_images_path, path));
        bindPlayer();
    }
}
