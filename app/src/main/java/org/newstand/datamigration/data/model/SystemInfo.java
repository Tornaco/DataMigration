package org.newstand.datamigration.data.model;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import lombok.Getter;
import lombok.experimental.Builder;

/**
 * Created by Nick on 2017/6/28 18:24
 */
@Builder
@Getter
public class SystemInfo {

    private String id, display, product, device, board, manufacturer, brand, model, bootloader, hardware, sdk, rel;

    public static SystemInfo fromSystem() {
        return SystemInfo.builder()
                .id(Build.ID)
                .display(Build.DISPLAY)
                .product(Build.PRODUCT)
                .device(Build.DEVICE)
                .board(Build.BOARD)
                .manufacturer(Build.MANUFACTURER)
                .brand(Build.BRAND)
                .model(Build.MODEL)
                .bootloader(Build.BOOTLOADER)
                .hardware(Build.HARDWARE)
                .sdk(String.valueOf(Build.VERSION.SDK_INT))
                .rel(Build.VERSION.RELEASE)
                .build();
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    @Nullable
    public static SystemInfo fromJson(@NonNull String json) {
        return new Gson().fromJson(json, SystemInfo.class);
    }
}
