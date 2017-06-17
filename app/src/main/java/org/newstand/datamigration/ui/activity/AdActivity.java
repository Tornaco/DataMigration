package org.newstand.datamigration.ui.activity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tbruyelle.rxpermissions2.RxPermissions;

import org.newstand.datamigration.R;
import org.newstand.datamigration.utils.EmojiUtils;

import io.reactivex.functions.Consumer;

/**
 * Created by Nick@NewStand.org on 2017/5/2 16:08
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class AdActivity extends TransitionSafeActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHomeAsUp();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setTitle(getString(R.string.title_show_ads, EmojiUtils.getEmojiByUnicode(0x1F60F)));
        } else {
            setTitle(getString(R.string.title_show_ads, ":)"));
        }
        setContentView(R.layout.layout_ad_container);

        requestPerms();

        loadAds();
    }

    private void loadAds() {

    }

    private void requestPerms() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.READ_PHONE_STATE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        // Ignored.
                    }
                });
    }
}
