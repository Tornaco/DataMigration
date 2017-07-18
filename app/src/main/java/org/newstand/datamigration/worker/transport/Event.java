package org.newstand.datamigration.worker.transport;

import android.support.annotation.StringRes;

import org.newstand.datamigration.R;

import lombok.Getter;

/**
 * Created by Tornaco on 2017/7/18.
 * Licensed with Apache.
 */

public enum Event {

    Prepare(R.string.permissive),
    ReadyToTransport(R.string.permissive);

    @Getter
    private
    @StringRes
    int description;

    Event(int description) {
        this.description = description;
    }
}
